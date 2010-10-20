function []=testPPM()
%%% testPPM.m ---
%%
%% Filename: testPPM.m
%% Description: Compares PPM cluster approximation vs Rank 1 approximation
%% Author: Vinay Jethava
%% Maintainer:
%% Created: Mon Jan 25 13:11:18 2010 (+0530)
%% Version:
%% Last-Updated: Thu Feb 25 15:52:39 2010 (+0100)
%%           By: Vinay Jethava
%%     Update #: 183
%% URL:
%% Keywords:
%% Compatibility:
%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%
%%% Commentary:
%%
%%
%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%
%%% Change log:
%%
%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%
%% This program is free software; you can redistribute it and/or
%% modify it under the terms of the GNU General Public License as
%% published by the Free Software Foundation; either version 3, or
%% (at your option) any later version.
%%
%% This program is distributed in the hope that it will be useful,
%% but WITHOUT ANY WARRANTY; without even the implied warranty of
%% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
%% GNU General Public License for more details.
%%
%% You should have received a copy of the GNU General Public License
%% along with this program; see the file COPYING.  If not, write to
%% the Free Software Foundation, Inc., 51 Franklin Street, Fifth
%% Floor, Boston, MA 02110-1301, USA.
%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%
%%% Code:

%%% PreSetup
warning off;
libPath = genpath('../lib');
addpath(libPath);
addpath ../code;

fprintf(2, ['testPPM(): Function that compares Rank 1 approximation ' ...
            'vs CVM approximation on synthetic dataset\n']  );

minT = 0; % time at which to begin simulation
tstep = 1; % time step increments - for gibbs sampling over c.t.
maxT = 10; % time at which to end simulation
nV = 10; % number of nodes in the graph
pE = 0.25; % pr(edge); average degree = (nV-1)*pE
G = erdosRenyi(nV, pE); % generate the graph
G = triu(G); % take only the upper half
G = G + G'; % symmetric graph i->j implies j->i
sW = [-1 1]'; % possible states that edges strengths can take
nW = length(sW); % number of states of W_ij
sX = [-1 1]'; % possible states that the nodes can take
nX = length(sX); % number of states of  X_i
[i, j, s] = find(G);
nE = length(i); % number of edges in the graph
E = [i j]; % adjacency list for the graph
clear i j s;
vX = []; % the values for X generated for each  time step in minT:maxT
vW = []; % the values for W generated for minT:stept:maxT

%%% Generate Some Clusters
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Let maximum size of clusters be clustered: let us do a bfs with
% that depth to find out the different clusters
% then if a node appears in expansion of two nodes - it is a shared
% node; this gives natural method for CVM cluster generation.
% NOTE: Equivalent to DFS at max depth 2
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Gtmp = G;  % a copy of the original graph
WHITE=0; GREY=1; BLACK=2;
nodeColors = zeros(nV, 1); % not seen any node so far.
clusters = struct('nodes', [], 'Q', []);
clusterNodes = {};
for i=1:nV % start looking at the nodes
    currCluster = [];
    if(nodeColors(i) == WHITE)
        currCluster = [currCluster i];
        nodeColors(i) = BLACK; % color me BLACK!
        [ci, adjNodesList, si]  = find(Gtmp(i, :));
        for j=1:length(adjNodesList)
            %%% Modification begins
            % if(nodeColors(j) == WHITE) % only mutually exclusive
            %%% Alternative
            if(nodeColors(j) ~= BLACK) % not already explored: BLACK
                                       %%% Modification ends
                nodeColors(j) = GREY;
                currCluster = [currCluster j];
            end
        end
        nClusters = length(clusterNodes);
        clusters(nClusters+1).nodes = currCluster;
        clusterNodes{nClusters+1} = currCluster;
    else
        continue;
    end
end
unassigned = ones(nE, 1);
clusters = clusters(2:length(clusters));
% decide the edges that are part of this cluster
for j=1:length(clusters)
    cedges = [];
    for e=1:nE
        isPresent = [false false];
        for i=1:2
            cv  = E(e, i);
            cnodes = clusters(j).nodes;
            for n=1:length(cnodes)
                if(cnodes(n) == cv)
                    isPresent(i) = true;
                end
            end
        end
        if(isPresent(1) && isPresent(2) && (length(cedges) < 5))
            cedges = [cedges; e];
            unassigned(e) = 0;
        end
    end
    clusters(j).edges = cedges;
end
clear currCluster cedges i j n isPresent cv cnodes;
remaining = find(unassigned); 
for nr=1:length(remaining)
    cindex = remaining(nr);
    ncl = length(clusters);
    clusters(ncl + 1).edges = [cindex];
    clusters(ncl + 1).nodes = [E(cindex, 1) E(cindex, 2)];
end
nClusters = length(clusters);
%%% Generate the Q matrix based on the cluster configuration
for i=1:nClusters
    currCluster = clusters(i).edges; % current cluster
    sCluster = length(currCluster); % number of nodes in cluster
                                    % the size of the Q matrix = nW^sCluster \times nW^sCluster
    cQ = rand(nW^sCluster, nW^sCluster);
    cQ = mk_stochastic(cQ);
    clusters(i).Q = cQ;
end
%%% Generate the data by doing gibbs sampling approach
wStart = sW(randi(nW, nE, 1));
wNext = getNextWtState(wStart, clusters, sW, 1);
%%% Do mean field approximation

%%% Make PPM cluster configuration - How does this relate to Q's

%%% Do ppm approximation

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Other functions that are needed
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [wNext] = getNextWtState(wCurr, clusters, sW, nIter)
if nargin < 4 % sample as many times as number of weights
    nIter  = length(wCurr);
end
if(size(wCurr, 1) == 1 ) % make it a column vector
    wCurr = wCurr';
end
nE = length(wCurr);
for l=1:nIter % pick one w_i to change
    idx = randi(nE);
    assert( (idx > 0) && (idx <= nE)) ;
    pAltVec = getProbForChange(wCurr, idx, clusters, sW);
    fprintf(2, 'testPPM(): idx: %d  prob: \n', idx ); disp(pAltVec); 
    
end
wNext = wCurr; % TODO: this needs to be updated

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [pAltVec] = getProbForChange(wCurr, idx, clusters, sW)
pAltVec = ones(length(sW), 1);
nE = length(wCurr); 
for i=1:length(clusters)
    cluster = clusters(i);
    isActive = false;  
    n = 0; 
    while( (~isActive) && (n < length(cluster.edges)))
        n = n + 1; 
        if(cluster.edges(n) == idx)
            isActive = true; 
        end
    end
    if(isActive)
        for cwidx = 1:length(sW)
            cw = sW(cwidx); 
           wAlt = [wCurr(1:(idx-1)) ; cw; wCurr((idx+1):nE) ];
           pCluster = getProbForChangeInCluster(wCurr, wAlt, cluster, sW);
           % fprintf(2, '\t testPPM(): pCluster: %g ', pCluster);  
           pAltVec(cwidx) = pAltVec(cwidx) * pCluster;            
        end
    end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [pCluster] = getProbForChangeInCluster(wCurr, wAlt, cluster, sW)
%% GETPROBFORCHANGEINCLUSTER returns the probability for the change in
%  the specified cluster.
wCurrRed = wCurr(cluster.edges); % The state in the cluster
wAltRed = wAlt(cluster.edges);
wCurrRedLarge = getLargeW(wCurrRed, sW);
wAltRedLarge = getLargeW(wAltRed, sW);
Q = cluster.Q;
pCluster = Q(wCurrRedLarge, wAltRedLarge);
fprintf(2, 'wCurr: %d wAlt: %d Q: %d x %d pCluster: %g\n', wCurrRedLarge, wAltRedLarge, ...
        size(Q, 1), size(Q, 2), pCluster);
% isInRange(wCurrRedLarge, size(Q, 1)) ;
% isInRange(wAltRedLarge, size(Q, 2)) ;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% testPPM.m ends here
