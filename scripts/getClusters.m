clear clusters; 
total = 0; 
numGenesInCluster = [];
for count = 1:8
    clear clist;
    i = 1 + 3*(count - 1) ;
    j = 2; 
    while((j <= 261) && (length(tmp{j, i}) > 0))
        clist{j-1} = tmp{j, i};
        j = j + 1; 
    end
    clusters{count} = clist; 
    total = total + j - 2;
    numGenesInCluster = [numGenesInCluster; j - 2];  
    disp(sprintf('Cluster: %d genes: %d total: %d', count, j-2, total));  
end
