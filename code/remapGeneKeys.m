function [indices, missing, present, failed_global] = remapGeneKeys(globalKeys, setToMap)
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
%%
%%% Code:
    indices = []; 
    missing = [];
    present = [];
    S = size(setToMap, 1);
    geneMap = containers.Map(); 
    for i=1:length(globalKeys) 
        if(ischar(globalKeys(i) ) || iscellstr(globalKeys(i)) )
            s = sprintf('%s', char(globalKeys(i))); 
            geneMap(upper(char(s))) = i;
        end
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
