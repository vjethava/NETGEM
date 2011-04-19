function [graphData] = readGraph(graphFile)
% READGRAPH    read the gene graph from file and save in 
%              NETGEM compatible structure
% 
% Usage: [graphData] = readGraph(graphFile)
% 
% Expects: 
% -------------
% graphFile:     File containing gene graph (default: '')
%
% Returns:
% -------------
% graphData:     Structure containing the following
%       * graphData.gene_names
%       * graphData.gene_graph
%       * graphData.gene_adj    Adjacency list of indices
%                               corresponding to gene_names 
%
% See Related: 
% --------------------
% * netgem.m 
%%
warning off; 
addpath ../data/exp2; 

if nargin < 1
  dataFile = ''; 
end

if length(dataFile) == 0
  fprintf(1, 'readGraph() Using gene graph from experiment 2');      
  addpath ../data/exp2;
  load graph.mat; 
else
  fprintf(1, 'readGraph() Put in code for reading gene graphs'); 
end
%%% The final structure containing the gene graph structure
% graphData = struct('gene_adj', gene_adj, 'gene_graph', gene_graph, ...
%                  'gene_names', gene_names); 
graphData =  struct('gene_adj', [], 'gene_graph', [], 'gene_names', []);
graphData.gene_adj = gene_adj; 
graphData.gene_names = gene_names;
graphData.gene_graph = gene_graph; 
end
