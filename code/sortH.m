function [sorted, changeProb, ha, hi] = sortH(H)
%%  CLASSIFYH classifies the class evolution matrices in terms of
%%  increasing degree of change exhibited by them.
sorted = struct('Qest', [], 'W', [], 'classes', [], 'A', [], ...
                'changeProb', []); 
nH = length(H.classes); 
nW = size(H.Qest, 1); 
if(nH > 0)
    changeProb = zeros(nH, 1); 
    for h=1:nH
        changeProb(h) = nW - trace(H.Qest(:, :, h)); 
    end
    [ha, hi] = sort(changeProb, 'descend');
    sorted.Qest = H.Qest(:, :, hi); 
    sorted.classes = H.classes(hi); 
    sorted.A = H.A(:, hi);
    sorted.W = H.W;
    sorted.changeProb = ha; 
end