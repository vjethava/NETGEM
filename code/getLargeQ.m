function [Qlarge] = getLargeQ(Qedges)
% GETLARGEQ returns the giant evolution probability matrix for the 
% original matrix. If there are nE edges and nW possible weight states 
% then, size(Qedges) = [nW, nW, nE] and the size of Qlarge = [nW^nE, nW^E]  
%
% Usage: [Qlarge] = getLargeQ(Qedges)
%
nW = size(Qedges, 1); 
assert(size(Qedges, 2) == nW); 
nE = size(Qedges, 3); 
Qlarge = Qedges(:, :, 1); 
for i=2:nE
    Qlarge = kron(Qlarge, Qedges(:, :, i)); 
end
