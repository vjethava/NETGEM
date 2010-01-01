function [G, H, FB, S] = exp2(sparsityFactor, W)
% EXP2 runs the experiment on the second dataset ( REF/MUT ) with 6 time
% points at different time intervals. We consider both the model with
% single Q over time period as well as Q(t) for each time point. 
% 
% Usage: [G, H, FB, S] = exp2(sparsityFactor)
%
% Expects:
% ------------
% 
%
% Returns:
% ------------
% H:            Structure containing the following
%                   * H.A:     The mixing proportions 
%                   * H.Qest:  The estimated evolution characteristics
%                              for each functional category, QclassGuess = ((nW x nW x
%                              nH)) where nW = number of weight states, nH = number of
%                              functional classes at level 3. 
%                   * nH: number of functional categories. 
%
% G:    `       Structure containing following items:
%                 * genes: the list of genes in cluster
%                 * E: The edge list (adjacency graph)
%                 * wML: the most probable assignment
% 
% FB:           Algorithm run details
%                 * FB.nIter: number of iterations
%                 * FB.LL: log likelihood 
% S:            Sorted result of analyze1()
%                 * S.E: list of named edges
%                 * S.W: the list of interaction strengths
%                 * S.var: the variance of change in weights 
%                          (descending sort order)
% 
    if nargin < 2
        W = [-2 -1 0 1 2];
    end
    
    if nargin < 1
        sparsityFactor = 1.0; 
    end
    interClassNoise = 0.1; 
    fbIters = 15;
    
    
    %%% PreSetup 
    warning off; 
    addpath ../lib/HMM;
    addpath ../lib/KPMtools;
    addpath ../lib/KPMstats;
    addpath ../netlab3.3;
    addpath ../code;
    addpath ../data/exp2; 
    
    disp(sprintf('\n**********************************************************************************************'));
    disp(sprintf('* exp2(): sparsityFactor: %g', sparsityFactor));
    disp(sprintf('**********************************************************************************************\n'));
    
    %%% load the dataset
    load cats.mat;
    load graph.mat; 
    load dataExp2.mat
    deleted = ['YLR403W'];
    nT = 6; 
    nS = 2; 
  
    nW = length(W);  
    
    %%% correct the graph valuation ?
    [i,j,s] = find(gene_graph);
    ne = length(i); 
    ns = size(gene_graph, 1); 
    gene_graph = sparse(i, j, ones(ne, 1), ns, ns); 
    clear ne ns i j s; 

    %%% update the classification graph explicitly
    gc_graph2 = sparse(gc_adj(:, 1), gc_adj(:, 2), ones(length(gc_adj), 1), 4715, 260);   
    gc_graph3 = gc_graph2(gc_gid_v, gc_cid_v);
    
    %%% find sub graph for sig_genes
    disp(sprintf('\nC0: Select subgraph for sig_genes'));
    disp(sprintf('*********************************************************')); 
    [i0, m0, p0] = remapGeneKeys(gene_names, sig_genes); 
    c0_graph = gene_graph(i0, i0); 
    c0_names = sig_genes(p0); 
    
    %%% find genes which have expression data. 
    disp(sprintf('\nC1: Select C0 genes with expr_data'));
    disp(sprintf('*********************************************************')); 
    [i1, m1, p1]  = remapGeneKeys(expr_genes, c0_names);
    c1_graph = c0_graph(p1, p1);
    c1_names = c0_names(p1);
    c1_data = expr_data(i1, :); 
    
    %%% find genes in cluster that have a classification
    disp(sprintf('\nC2: Select C1 genes with functional category'));
    disp(sprintf('*********************************************************')); 
    [i2, m2, p2] = remapGeneKeys(gc_gid_k, c1_names); 
    % [c1_names(i2)  gc_gid_k(p2)] %% these must be the same
    c2_graph = c1_graph(p2, p2); 
    c2_names = c1_names(p2);
    c2_data = c1_data(p2, :); 
    c2_classes = gc_graph3(i2, :);
    
    %%% find the diffusion factor.
    disp(sprintf('\nCompute the diffusion factor for %s', deleted));
    disp(sprintf('*********************************************************')); 
    [i3, m3, p3] = remapGeneKeys(c2_names, deleted); 
    nV = length(c2_names); 
    DF = zeros(nV, 2); % two strains - REF and MUT
    DF(:, 2) = knockout2(c2_graph, i3);
    [I, J, S1] = find(c2_graph);
    nE = length(I); 
    class_names = gc_cid_k;
    %%% prior for class transition probabilities 
    
    X =zeros(nV, nT, nS); 
    X(:, :, 1) = c2_data(:, 1:nT); 
    X(:, :, 2) = c2_data(:, (nT+1):(2*nT));
    %%% normalize in life else all -1's
    for s=1:nS
        X(:, :, s) = X(:, :, s) - mean(mean(X(:, :, s)));
    end
    
    c2_E = [I J];
    c2_edges = c2_classes(I, :) + c2_classes(J, :);
    nH = size(c2_classes, 2); 
    %%% remove irrelevant classes
    c2_c = [];
    c2_i = [];
    for h=1:size(c2_classes, 2) 
        if(nnz(c2_classes(:, h)) > 0)
            c2_c = [c2_c c2_classes(:, h)];
            c2_i = [c2_i h]; 
        end
    end
    c2_classes = c2_c; 
    class_names = gc_cid_k(gc_cid_v(c2_i) );

    %    keyboard;
    nH = size(c2_classes, 2); 
    c2e = [];
    for h=1:nH
        if(nnz(c2_edges(:, h)) > 0)
            c2e = [c2e c2_edges(:, h)]; 
        end
    end
    c2_edges = c2e; 
    nH = size(c2_edges, 2); 
    c2_edgesNoisy = mk_stochastic(c2_edges + interClassNoise*rand(nE, nH) ) ; 
    c2_edges2 = mk_stochastic(c2_edges); 
  
    Qprior = ones(nW, 1)*exp(-3.*abs(W).^sparsityFactor).*ones(nW, nW);
    Qprior = mk_stochastic(Qprior); 
    QclassGuess = zeros(nW, nW, nH); 

    for i=1:nH
        for j=1:nW
            QclassGuess(j, :, i) = dirichletrnd(Qprior(j, :) );
            % QclassGuess(j, :, i) = Qprior(j, :) ;   
        end
    end
    QedgeGuess = getEdgeQfromClassQ(QclassGuess, c2_edges, Qprior);
    pW0 = 1/nW*ones(nW, nE); % prior at time t=0
    xiClass = QclassGuess;
    xi0  = QedgeGuess;
    %%% Run the forward-backward algorithm 
    ll0 = 0.0;
    beliefsChanged = true;
    nIter = 1; 
    LL= [];
    disp(sprintf('\nStarting forward backward iterations\n'));
    while ((beliefsChanged) && (nIter < fbIters))
        beliefsChanged = false; 
        [f, b, xi, ll, wML] = fbEdge(X, c2_E, DF, W, xi0, pW0);
        disp(sprintf('fb-iteration: %d log likelihood: %g' , nIter,ll)); 
        xiClass = getClassQfromEdgeQ(xi, c2_edges, Qprior, QclassGuess); 
        QedgeGuess = getEdgeQfromClassQ(xiClass, c2_edges, Qprior, QedgeGuess); 
        xi0 = QedgeGuess; 
        QclassGuess = xiClass;
        nIter = nIter + 1;
        if(abs(ll - ll0) > 1e-2)
            ll0 = ll;
            beliefsChanged = true; 
        end
        LL = [LL; ll];
    end
    nIter = nIter - 1; 
%%% Look at some class Q's 
    %     for i=1:10
%         h = unidrnd(nH); 
%         e = unidrnd(nE);
%         disp(sprintf(' Q_class: %d\t\t\t\tQ_edge: %d', h, e)); 
%         disp([QclassGuess(:, :, h) QedgeGuess(:, :, e)]); 
%     end

%%% Put out the output
    G = struct('E', [], 'wML', [], 'genes', []);
    H = struct('A', [], 'Qest', [], 'classes', [], 'W', []);  
    FB = struct('nIters', [], 'LL', []);
    G.E = c2_E; G.wML = wML; G.genes = c2_names; 
    H.A = c2_edges; H.Qest = QclassGuess; H.classes = class_names; 
    FB.nIters = nIter; FB.LL = LL;
    S = analyze1(G, H, FB); 
%    keyboard; 
end
    

    
    