%%% getFuncCatEvol.m --- 
%% 
%% Filename: getFuncCatEvol.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Thu Jan  7 23:53:55 2010 (+0530)
%% Version: 
%% Last-Updated: Fri Jan  8 00:16:48 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 12
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
clear; 
load ../results/result2j_sf2.mat; 
load ../data/dataset8/cats.mat; 
S2 = sj;
G2 = gj; 
H2 = hj; 
T = size(S2.W, 2); 
nH = length(H2.classes); 
nE = size(H2.A, 1); 
hClassesEvol = zeros(nH, nH, T);
for e=1:nE
    disp(sprintf('doing for edge %d', e)); 
    [vals, indices] = find(H2.A(e, :) );  
    for i=1:length(indices)
        for j=(i+1):length(indices)
            hClassesEvol(i, j, :) =  hClassesEvol(i, j, :) + ...
                reshape(S2.W(e, :), 1, 1, T);
        end
    end
end

    

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% getFuncCatEvol.m ends here
