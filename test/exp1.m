function [G, H, FB]=exp1(tCluster, sparsityFactor, isAnaerobic)
%% EXP1 Runs experiment with fixed mixture proportions for  
%% the target cluster in {1,...,8} (8 clusters identified)
% 
% 
%  Usage: [G, H, FB]=exp1(tCluster, sparsityFactor, isAnaerobic)
%
%  Expects:
% -------------
%  tCluster: The identity of the cluster 
%  isAnaerobic:  If isAnaerobic = 1 => Do analysis for anaerobic
%                               = 0 => Do analysis for aerobic  
%  sparsityFactor: controls how likely the weight are to be off >= 1 
% 
%  Returns:
% -------------
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
%                 * graph: the adjacency graph
%                 * isAnaerobic: whether aerobic or not 
%                 * E: The edge list (adjacency graph)
%                 * wML: the most probable assignment
% 
% FB:           Algorithm run details
%                 * FB.nIter: number of iterations
%                 * FB.LL: log likelihood

%%% PreSetup 
    warning off; 
    addpath ../lib/HMM;
    addpath ../lib/KPMtools;
    addpath ../lib/KPMstats;
    addpath ../netlab3.3;
    addpath ../code;
    addpath ../data/dataset8; 
   
%%% Initialization
    interClassNoise = 0.1; 
    fbIters = 40; 
    if nargin < 3
        isAnaerobic = 0; 
    end
    load expr.mat; 
    load graph.mat;
    if isAnaerobic
        load an_cluster.mat;
    else
        load cluster.mat;
    end
    assert((tCluster >= 1) && (tCluster <= 8), 'tCluster should be between 1 and 8 inclusive'); 
    load cats.mat; 
    
    %%% correct the graph valuation ?
    [i,j,s] = find(gene_graph);
    ne = length(i); 
    ns = size(gene_graph, 1); 
    gene_graph = sparse(i, j, ones(ne, 1), ns, ns); 
    clear ne ns i j s; 

    %%% update the classification graph explicitly
    gc_graph2 = sparse(gc_adj(:, 1), gc_adj(:, 2), ones(length(gc_adj), 1), 4715, 260);   
    gc_graph3 = gc_graph2(gc_gid_v, gc_cid_v);
    

    %%% find genes for which we have expression data
    disp(sprintf('\nC0: Select genes present in the interaction network for which expression data is present')); 
    [i0, m0, p0] = remapGeneKeys(expr_genes, gene_names); 
    c0_graph = gene_graph(p0, p0); 
    c0_names = expr_genes(i0); 

    %%% find graph over genes in cluster 
    disp(sprintf('\nC1: Select genes in cluster %d present in C0', tCluster)); 
    [i1, m1, p1]  = remapGeneKeys(c0_names, clusters{tCluster}');
    % [gene_names(i1)  clusters{1}(p1)'] %% these must be the same
    c1_graph = c0_graph(i1, i1);
    c1_names = c0_names(i1);

    %%% find genes in cluster that have a classification
    disp(sprintf('\nC2: Select genes in C1 that have a functional category')); 
    [i2, m2, p2] = remapGeneKeys(c1_names, gc_gid_k); 
    % [c1_names(i2)  gc_gid_k(p2)] %% these must be the same
    c2_graph = c1_graph(i2, i2); 
    c2_names = c1_names(i2); 
    c2_classes = gc_graph3(i2, :);
    if(isAnaerobic)
        dtype = sprintf('anaerobic');
    else
        dtype = sprintf('aerobic');
    end
    disp(sprintf('\nC3: Select %s scenario gene expression data for genes in C2', dtype));  
    [i3, m3, p3] = remapGeneKeys(expr_genes, c2_names);
    c2_data = expr_data(i3, :, :); 
    
    [I, J, S] = find(c2_graph); 
    c2_E = [I J];
    %%% Initialize local variables
    W = [-1 0 +1];
    nE = length(I); 
    class_names = gc_cid_k; 
    %% Remove irrelevant names 
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
    nV = length(c2_names); 
    nT = 8; 
    nW = length(W); 

    data_ae = c2_data(:, 1:nT); 
    data_an = c2_data(:, (nT+1):(2*nT)); 
    
    %%% normalize the data so that [-1 0 1] starts making sense
    if isAnaerobic
        X = data_an - mean(mean(data_an));
    else
        X = data_ae - mean(mean(data_ae));
    end
    
    % data_ae = 2*data_ae/max(range(data_ae)); 

    c2_edges = c2_classes(I, :) + c2_classes(J, :);  
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
%     save run2.mat;
%     keyboard; 
%    load run2.mat;
    
    Qprior = (1/nW)*ones(nW, nW);
    Qprior(:, 2) = sparsityFactor*Qprior(:, 2);  
    Qprior = mk_stochastic(Qprior); 
    QclassGuess = zeros(nW, nW, nH); 

    for i=1:nH
        for j=1:nW
            QclassGuess(j, :, i) = dirichletrnd(Qprior(j, :) );
            % QclassGuess(j, :, i) = Qprior(j, :) ;   
        end
    end
    QedgeGuess = getEdgeQfromClassQ(QclassGuess, c2_edges, Qprior); 
    DF = zeros(nV, 1); 
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
        if(abs(ll - ll0) > 1e-3)
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
    G = struct('E', [], 'wML', [], 'isAnaerobic', [], 'genes', [], 'cluster', []);
    H = struct('A', [], 'Qest', [], 'classes', []);  
    FB = struct('nIters', [], 'LL', []);
    G.cluster = tCluster; 
    G.E = c2_E; G.wML = wML; G.isAnaerobic = isAnaerobic; G.genes = c2_names; 
    H.A = c2_edges; H.Qest = QclassGuess; H.classes = class_names; 
    FB.nIters = nIter; FB.LL = LL; 
    
%    keyboard; 
end
