function [status, errcode] = cytowrite1(expName, S, G,  score)
% CYTOWRITE1 converts the data and writes out the cytoscape compatible files. 
%
% Usage: [status, errcode] = cytowrite(fileName, W, E, A, Hnames, ExprData, ExprGenes)
% 
%
    addpath ../code; 
    if nargin < 4 
        score = 0.0; 
    end
    load ../data/orf.mat; 
    load ../data/expr.mat
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    %%% SIF file generation
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    graphFileName = sprintf('%s.sif', expName); 
    %%% write out the graph structure
    sid = fopen(graphFileName, 'w'); 
    E = S.E; 
    for i=1:size(E, 1)
        disp(sprintf(['cytowrite1(): Write Graph Structure edge: %d ' ...
                      'of %d'], i, size(E, 1))); 
        if(S.var(i) >= score)
            %%% if we want common names
            % gi = char(E(i, 1));
            % gj = char(E(i, 2));
            %%% if we want systematic names
            gi = char(G.genes(G.E(i, 1)));
            gj = char(G.genes(G.E(i, 2))); 
            fprintf(sid, '%s pp %s\n', gi, gj);
        end  
    end
    fclose(sid); 
    %%% right out the expression levels for the relevant genes 
    curr_genes = G.genes;
    [ci, cm, cp] = remapGeneKeys(orf_systematic, curr_genes); 
    for i=1:length(ci) % use common name if possible 
      % curr_genes(cp(i) ) = orf_common( ci(i) );
        curr_genes(cp(i) ) = orf_systematic( ci(i) );       
    end
        
    [nfname, msg] = sprintf('%s.noa', expName);
    [nid] = fopen(nfname, 'w');  
    fprintf(nid, 'ExpressionLevel\n');     
    
    fclose(nid); 
    
    %%% write out the edge interactions and node expression levels
    T = size(S.W, 2); 

    [efname, msg] = sprintf('%sw.eda', expName);

    [eid] = fopen(efname, 'w'); 
    
    fprintf(eid, 'InteractionStrength\n');
     %    fprintf(cid, 'edge.color\n');
    for i=1:size(E, 1)
       disp(sprintf('cytowrite1(): Edge %d of %d', i, size(E, 1)));
       if(S.var(i)>=score)
           %%% if we want common names
           % cx = char(E(i, 1)); 
           % cy = char(E(i, 2)); 
           %%% if we want systematic names
           cx = char(G.genes(G.E(i, 1))); 
           cy = char(G.genes(G.E(i, 2)));      
           fprintf(eid, '%s (pp) %s =', cx, cy);      
           for t=1:T
               cw = S.W(i, t); 
               fprintf(eid, ' %d', cw);
               % if(t < (T))
               %     fprintf(eid, ','); 
               % end
           end
           fprintf(eid, '\n'); 
       end
    end           
    fclose(eid); 
    %  [cfname, msg] = sprintf('%s_c.eda', expName); 
    %  [cid] = fopen(cfname, 'w'); 

    % for t=1:T    
    %     disp(sprintf('Hi there t: %d', t));  
    %     for i=1:length(final_genes)
    %         cx = final_genes(i); 
    %         fprintf(nid, '%s = %g\n', char(cx), final_data(i, t));
    %     end
    %     fclose(nid); 
    %     % code changed from w_ml to Wsign (avgd over NTrials)
    %     for i=1:size(Wsign, 1) 
    %         x = Runs(NI).p(i).i;
    %         y = Runs(NI).p(i).j;
    %         cx = char(final_genes(x)); 
    %         cy = char(final_genes(y)); 
    %         fprintf(eid, '%s (pp) %s = %d\n', cx, cy, Wsign(i, t));
    %         switch(Wsign(i, t))
    %           case -2
    %             color = '255, 153, 153';
    %           case -1
    %             color = '255, 0, 0'; 
    %           case 0
    %             color = '255, 255, 204'; % pale yellow
    %           case 1
    %             color = '51, 204, 255'; % light blue
    %           case 2
    %             color = '51, 51, 255'; % dark blue
    %         end
    %         fprintf(cid, '%s (pp) %s = %s\n', cx, cy, color); 
            
    %     end
    %     fclose(eid);
    %     fclose(cid); 
    % end

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    %% Dot file generation
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    % for t=1:T
    %     [fname, msg] = sprintf('exp2_t_%d.dot', t);
    %     [fid] = fopen(fname, 'w'); 
    %     fprintf(fid, 'graph exp2_%d {\n', t);
    %     %for i=1:length(final_genes)
    %     %    fprintf(fid, 'X%d [label="%s"]\n', i, char(final_genes(i)));
    %     %end
    %     for i=1:length(w_ml) 
    %         x = Runs(NI).p(i).i;
    %         y = Runs(NI).p(i).j;
    %         cx = char(final_genes(x)); 
    %         cy = char(final_genes(y)); 
    %         if( x ~= y)
    %             if (w_ml(i, t) > 0)
    %                 fprintf(fid, '"%s" -- "%s" [label="+1"]\n', cx, cy);
    %             elseif(w_ml(i, t) < 0)
    %                 fprintf(fid, '"%s" -- "%s" [label="-1"]\n', cx, cy);
    %             end
    %         end
    %     end
    %     fprintf(fid, '}\n'); 
    %     fclose(fid);     
    % end

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% runExp1.m ends here
end