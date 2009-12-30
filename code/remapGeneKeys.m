function [indices, missing, present] = remapGeneKeys(globalKeys, setToMap)
% REMAPGENEKEYS maps the genes present in setToMap to the indices
% at which they are found in the globalKeys set. It raises an error
% in case of missing gene in the globalKeys.
%
% Usage: [indices, missing, present] = remapGeneKeys(globalKeys, setToMap)
%  
% Returns:
% --------
% indices - the indices of the setToMap in the global database
% missing - the set of indices corresponding to genes that have
%          been listed in setToMap but are missing in the database
% 
% Expects: 
% --------
% globalKeys - the list of genes in the database
% setToMap - the set of genes which are to be mapped to the database
%
    
%%% remapGeneKeys.m --- 
%% 
%% Filename: remapGeneKeys.m
%% Description: 
%% Author: Vinay Jethava    
%% Maintainer: 
%% Created: Tue Nov 24 14:50:01 2009 (+0100)
%% Version: 
%% Last-Updated: Wed Nov 25 16:44:15 2009 (+0100)
%%           By: Vinay Jethava
%%     Update #: 35
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
    indices = []; 
    missing = [];
    present = [];
    S = size(setToMap, 1);
    geneMap = containers.Map(); 
    for i=1:length(globalKeys) 
        geneMap(upper(char(globalKeys(i)))) = i;
    end 
    
    for i =1:S
        if(S == 1)
            cgene = upper(char(setToMap));
        else
            cgene = upper(char(setToMap(i)));
        end
        j = 1;
         notFound = true;
         % while(notFound && (j < length(globalKeys)))
         %     ckey = char(globalKeys(j));
         %     result = strcmp(ckey, cgene); 
         %     disp(sprintf('key: %s gene: %s result: %d\n', ckey, ...
         %                  cgene, result)); 
             
         %     if(result)
         %         notFound = false; 
         %         %      indices = [indices; j];
         %     else
         %         j = j+1; 
         %     end
         % end
         found = isKey(geneMap, cgene); 
         notFound = ~found; 
        if(notFound)
            %    disp(sprintf(['remapGeneKeys(): missing gene: %s (%d ' ...
            %               'of %d)'], cgene, i,  S));
            missing = [missing; i];
        else
            present = [present; i]; 
            j = geneMap(cgene); 
            indices = [indices; j];
        end
    end
    disp(sprintf(['remapGeneKeys(): database: %d total genes: %d ' ...
                  'found: %d missing: %d'] , length(globalKeys), ...
                 S, length(indices), length(missing)));   
end      
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% remapGeneKeys.m ends here
