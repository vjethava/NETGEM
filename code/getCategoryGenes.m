function [indices] = getCategoryGenes(category, cat_keys, cat_vals)
% GETCATEGORYGENES gives the genes restricted to a certain
% category where the mapping of a gene to multiple categories is
% allowed, given by the cat_key -> cat_vals. 
% 
% Usage:  [indices] = getCategoryGenes(category, cat_keys, cat_vals)    
%
% Expects:
% -------
% cat_keys: the ordered set of keys.
% cat_vals: matrix of categories that each gene can belong to.
% category: required category to look at.
    
%%% getCategoryGenes.m --- 
%% 
%% Filename: getCategoryGenes.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Tue Nov 24 11:35:21 2009 (+0100)
%% Version: 
%% Last-Updated: Wed Nov 25 15:20:17 2009 (+0100)
%%           By: Vinay Jethava
%%     Update #: 33
%% URL: 
%% Keywords: 
%% Compatibility: 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Commentary: 
%%  Extracts the genes belonging to a specific category
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
    indices = [];
    disp(sprintf('getCategoryGenes(): for category: %d', category));
    for i=1:length(cat_keys)
        j=1;
        not_found = true;
        not_nan = true; 
        while(not_found && not_nan && (j < size(cat_vals, 2))) 
            if(isnan(cat_vals(i, j))) 
                not_nan = true;
            elseif(cat_vals(i, j) == category)
                %    disp(sprintf('getCategoryGenes(): found gene(%d): %s for category: %d', i, char(cat_keys(i)), category));
                not_found = false;
                indices = [indices; i];
            end
            j = j + 1;
        end
    end
    disp(sprintf(['getCategoryGenes(): found %d genes for category: ' ...
    '%d'], length(indices), category));
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% getCatGraph.m ends here
