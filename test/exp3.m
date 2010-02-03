function []=exp3(resName, listOfFiles)
% resName = 'top1'; 
% listOfFiles = {'top1'};
W = [-2 -1 0 1 2];
sparsityFactor = 1.0;
% if nargin < 2
%     W = [-2 -1 0 1 2];
% end

% if nargin < 1
%     sparsityFactor = 1.0;
% end

interClassNoise = 0.1;
fbIters = 15;
%%% PreSetup
warning off;
addpath ../lib/HMM;
addpath ../lib/KPMtools;
addpath ../lib/KPMstats;
addpath ../netlab3.3;
addpath ../code;
addpath ../data/current; % the data corresponding to PNAS paper

nT = 6;
nS = 6;
nW = length(W);

load cats.mat;
load graph.mat;
load expression.mat;

expr_genes = genes;
expr_cond = {'elm'; 'ref'; 'reg'; 'sak'; 't210a'; 'tos'};
expr_data = zeros(5545, 6, 6);
expr_data(: , :, 1) = elm;
expr_data(: , :, 2) = ref;
expr_data(: , :, 3) = reg;
expr_data(: , :, 4) = sak;
expr_data(: , :, 5) = t210a;
expr_data(: , :, 6) = tos;
expr_deletions{1} = {'YER129W'; 'YGL179C'};
expr_deletions{2} ={};
expr_deletions{3} = {'YDR028C'};
expr_deletions{4} = {'YKL048C'; 'YGL179C'};
expr_deletions{5} = {'YDR477W'};
expr_deletions{6} = {'YKL048C'; 'YER129W'};
% save expr.mat expr_cond expr_genes expr_data expr_deletions;

%%% Load the relevant list of genes that you want to look at.
%%%
%%% Initialliy assuming all genes.
if(size(listOfFiles, 1) == 1) % only one list of genes
    fname = sprintf('%s.txt', listOfFiles{1});
    cgenes = importGenesFile(fname);
    target_genes = cgenes;
else
    fname = sprintf('%s.txt', listOfFiles{1});
    cgenes = importGenesFile(fname);
    target_genes = cgenes;
    for count = 2:length(listOfFiles) 
        fname = sprintf('%s.txt', listOfFiles{count});
        cgenes = importGenesFile(fname);
        [ig, mg, pg] = remapGeneKeys(target_genes, cgenes); 
        lt = length(target_genes);
        for j=1:length(mg)
            target_genes{lt+j} = cgenes{mg(j)}; 
        end
    end
end


%%% update the classification graph explicitly
gc_graph2 = sparse(gc_adj(:, 1), gc_adj(:, 2), ones(length(gc_adj), 1), 4715, 260);
gc_graph3 = gc_graph2(gc_gid_v, gc_cid_v);

%%% find sub graph for the target genes under focus
disp(sprintf('\nC0: Select subgraph for target_genes'));
disp(sprintf('*********************************************************'));
[i0, m0, p0] = remapGeneKeys(gene_names, target_genes);
c0_graph = gene_graph(i0, i0);
c0_names = target_genes(p0);

%%% find genes which have expression data.
disp(sprintf('\nC1: Select C0 genes with expr_data'));
disp(sprintf('*********************************************************'));
[i1, m1, p1]  = remapGeneKeys(expr_genes, c0_names);
c1_graph = c0_graph(p1, p1);
c1_names = c0_names(p1);
n1 = length(c1_names);
c1_data = zeros(n1, nT, nS);
for s=1:nS
    c1_data(:, :, s)  = expr_data(i1, :, s);
end

%%% find genes in cluster that have a classification
disp(sprintf('\nC2: Select C1 genes with functional category'));
disp(sprintf('*********************************************************'));
[i2, m2, p2] = remapGeneKeys(gc_gid_k, c1_names);
% [c1_names(i2)  gc_gid_k(p2)] %% these must be the same
c2_graph = c1_graph(p2, p2);
c2_names = c1_names(p2);
n2 = length(c2_names);
c2_data = zeros(n2, nT, nS);
for s=1:nS
    c2_data(:, :, s)  = c1_data(p2, :, s);
end
c2_classes = gc_graph3(i2, :);

%%% find the diffusion factor.
nV = length(c2_names);
DF = zeros(nV, nS); % diffusion factor for the strains.
[I, J, S1] = find(c2_graph);
nE = length(I);
class_names = gc_cid_k;
for s=1:nS
    disp(sprintf(['\nCompute the diffusion factor for strain %s ' ...
                  'with deletions '], expr_cond{s}));
    disp(expr_deletions{s}');
    [i3, m3, p3] = remapGeneKeys(c2_names, expr_deletions{s});
    disp(sprintf('************************************************'));
    DF(:, s) = knockout2(c2_graph, i3);
end

X = c2_data;
for s=1:nS
    X(:, :, s) = X(:, :, s) - mean(mean(X(:, :, s)));
end

c2_E = [I J];
c2_edges = c2_classes(I, :) + c2_classes(J, :);
nH = size(c2_classes, 2);
%%% remove irrelevant classes
fprintf(2, 'Removing irrelevant classes\n');
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
fprintf(2, 'Assigning classes to edges\n');
nH = size(c2_classes, 2);
c2e = [];
c2index1 = [];
for h=1:nH
    if(nnz(c2_edges(:, h)) > 0)
        c2e = [c2e c2_edges(:, h)];
        c2index1 = [c2index1; h];
    end
end
c2_edges = c2e;
class_names = class_names(c2index1);
nH = size(c2_edges, 2);
c2_edgesNoisy = mk_stochastic(c2_edges + interClassNoise*rand(nE, nH) ) ;
c2_edges2 = mk_stochastic(c2_edges);

Qprior = ones(nW, 1)*exp(-abs(W).^sparsityFactor).*ones(nW, nW);
Qprior = mk_stochastic(Qprior);
QclassGuess = zeros(nW, nW, nH);

fprintf(2, 'Assigning dirichlet priors to class transition probabilities\n');
for i=1:nH
    for j=1:nW
        QclassGuess(j, :, i) = dirichletrnd(Qprior(j, :) );
        % QclassGuess(j, :, i) = Qprior(j, :) ;
    end
end
fprintf(2, 'Generate the edge Q for class Q\n'); 
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

%%% The cutoff for exp polyfit is not working - too large values. 
%% cutoffScore  = getExpFitScore(S.vW, 6);  
%%% Instead using the % of total estimate. 
pct1 = 0.9; % take in >= 90% of the probability mass of change  
tot1 = sum(S.vW); 
cur1 = 0.0; 
score = S.vW(1); 
cS = S.vW(1); 
count = 1; 
while ((cur1 < pct1*tot1) || (score == S.vW(count) ))
    score = S.vW(count);
    cur1 = cur1 + S.vW(count); 
    count = count + 1; 
end
%%% just to take in the values on the margin 
score = score - 1e-6; 

%%% Write it out to cytowrite. 
mkdir(sprintf('../results/%s', resName)); 
mkdir(sprintf('../results/%s', resName)); 
mkdir(sprintf('../results/%s/edgeColor', resName)); 
mkdir(sprintf('../results/%s/images', resName));
% cytowrite(dname, sj, gj, score, expr_data, expr_genes,
% expr_legend);
expr_legend = {'T1'; 'T2'; 'T3'; 'T4' ; 'T5'; 'T6'}; 
cytowrite(resName, S, G, score, expr_data, expr_genes, expr_legend); 

