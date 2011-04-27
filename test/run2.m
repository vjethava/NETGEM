%%% run2.m --- 
%% 
%% Filename: run2.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Wed Jan  6 22:55:04 2010 (+0530)
%% Version: 
%% Last-Updated: Thu Jan  7 01:26:24 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 20
%% URL: 
%% Keywords: 
%% Compatibility: 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Commentary: 
%% 
%% 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Change log:
%% 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%% This program is free software; you can redistribute it and/or
%% modify it under the terms of the GNU General Public License as
%% published by the Free Software Foundation; either version 3, or
%% (at your option) any later version.
%% 
%% This program is distributed in the hope that it will be useful,
%% but WITHOUT ANY WARRANTY; without even the implied warranty of
%% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
%% General Public License for more details.
%% 
%% You should have received a copy of the GNU General Public License
%% along with this program; see the file COPYING.  If not, write to
%% the Free Software Foundation, Inc., 51 Franklin Street, Fifth
%% Floor, Boston, MA 02110-1301, USA.
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Code:
Names = {'REF' ;'MUT'}; 
load ../data/expr2.mat; 
sf = 2.0; 
%%% Code generation for the joint case
[gj, hj, bj, sj] = exp2(sf); 
[score, freqC, binsC] = getExpFitScore(sj.vW, 6, 0.05); 

dname = 'JOINT';
mkdir(sprintf('../results/%s',dname)); 
mkdir(sprintf('../results/%s/edgeColor', dname)); 
mkdir(sprintf('../results/%s/images', dname));
cytowrite(dname, sj, gj, score, expr_data, expr_genes, expr_legend); 
hjSorted = sortH(hj); 
save ../results/result2j_sf2.mat; 
%%% Code generation for singletons
[gs, hs, bs, ss] = exp2_diffStrains( sf );
sScore = zeros(2, 1); 
for i = 1:2
    dname = char(Names(i));
    mkdir(sprintf('../results/%s',dname)); 
    mkdir(sprintf('../results/%s/edgeColor', dname)); 
    mkdir(sprintf('../results/%s/images', dname));
    egenes = expr_genes; 
    %   hsSorted(i) = sortH(hs(i)); 
    switch i
      case 1  
        edata = expr_data(:, 1:6); 
        elegend = expr_legend(1:6);
      otherwise
        edata = expr_data(: , 7:12); 
        elegend = expr_legend(7:12); 
    end
    sScore(i) = getExpFitScore(ss(i).vW, 6); 
    cytowrite(dname, ss(i), gs(i), sScore(i), edata, egenes, elegend); 
end
save ../results/result2s_sf2.mat;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% run2.m ends here
