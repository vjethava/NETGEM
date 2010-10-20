%% Analyses the results in terms of eigen vectors. 
Vq = [];
Vqt = [];
Vtt = [];
for i=1:length(Q_results),
    [v1, d] = eig(Q_results(i).Q');
    [v2, d] =  eig(Q_results(i).Qt');
    [v3, d] = eig(Q_results(i).Q_tilde'); 
    %  pause(1);
    vq = v1(:, 1); 
    vqt =v2(:, 1); 
    vtt =v3(:, 1);  
    s1 = 0; s2 = 0; s3 = 0; 
    for j=1:3,
        s1 = s1 + vq(j); 
        s2 = s2 + vqt(j);
        s3 = s3 + vtt(j); 
    end
    if(abs(s1) > 1e-9)
        vq = vq/s1;
    end
    
    if(abs(s2) > 1e-9)
        vqt = vqt/s2;
    end
    
    if(abs(s3) > 1e-9)
        vtt = vtt/s3;
    end
   
    Vq = [Vq  vq];
    Vqt = [Vqt vqt];
    Vtt = [Vtt vtt];
    
    disp([vq vtt vqt inf*ones(3, 1) vqt*(1 - vq(2)) ]); 
    pause(1);
end

    