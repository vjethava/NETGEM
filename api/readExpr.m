function [exprData] = readExpr(dataFile)
% READEXPR     read the expression data from file and save in
%              NETGEM compatible structure
% 
% Usage: [expr_data, expr_genes, expr_cond] = readExpr(dataFile)
% 
% Expects: 
% -------------
% dataFile:     File containing gene expression data
%
% Returns:
% -------------
% exprData:     Structure containing the following
%       * exprData.expr_cond    Vector containing names of the conditions
%                               at which the expression data was measured
%       * exprData.expr_data    The matrix containing expression data
%       * exprData.expr_genes   The list of genes (systematic
%                               notation) for which the expression
%                               data was measured 
%
% See Related: 
% --------------------
% * netgem.m 
%%
if nargin < 1
  dataFile = ''; 
end
% warning off; 
if length(dataFile) == 0
  addpath ../data/exp2;
  load dataExp2.mat; 
else
  fprintf(1, ['readExpr() Put in code for reading general expression ' ...
              'data files']); 
end

%%% The final structure containing the gene expression data  
exprData = struct('expr_genes', [], 'expr_data', [], 'expr_cond', []);
% NOTE: constructor initialization causing problem ? Using manual:  
exprData.expr_genes = expr_genes;
exprData.expr_cond = expr_cond;
exprData.expr_data = expr_data; 

end