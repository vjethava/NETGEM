function [G1,G2,conv] = itermatinv(G0,V,tol,maxit)
% Compute graph kernel G1 and G2 
% G1: similarity rank matrix
% G2: dissimilarity rank matrix

% G = (I - G0*V)^(-1)*G0
%   = G0 + G0*V*G0 + G0*V*G0*V*G0 + G0*V*G0*V*G0*V*G0 + ...;
% Let G = G1 + G2 where
% G1 = G0 +      G0*V*G0*V*G0 +      G0*V*G0*V*G0*V*G0*V*G0 + ...;
% G2 = G0*V*G0 + G0*V*G0*V*G0*V*G0 + G0*V*G0*V*G0*V*G0*V*G0*V*G0 + ...;
% G1 = G0 + G0*A*G0*A*G1      => G1 = (I - G0*A*G0*A)^(-1)*G0
% G2 = G0*V*G0 + G0*V*G0*V*G2 => G2 = (I - G0*A*G0*A)^(-1)*G0*A*G0 

% Let M = (I - G0*V*G0*V)^(-1)
%       = I + G0*V*G0*V + (G0*V*G0*V)^2 + (G0*V*G0*V)^3
% M(k+1) = I + G0*V*G0*V*M(k)

nv = size(G0,1);
H = G0*V*G0*V;
sprintf('starting iterations\n')
% initial M = I; 
I = eye(nv);
M = cell(2,1);
M{1} = I;
M{2} = zeros(nv);
old = 1;
new = 2;
frac1 = zeros(1,maxit);
conv = zeros(1,maxit);

for i = 1:maxit
    M{new} = I + H * M{old};
    diff1 = sum( sum(abs(M{new} - M{old}) )) / (nv * nv); % mean diff for element
    mval1 = max(max(abs(M{new})));
    frac1(i) = diff1 / mval1;      % aver diff/ max matrix element
    
    % If ref = (I - H) is rank deficient, 
    % M is the pseudo-inverse of ref, i.e. ref*M*ref = ref
    ref = I - H; refp = ref*M{new}*ref;
    pseudoinvdiff = max(max(abs(refp-ref)));
    conv(i) = pseudoinvdiff/max(max(max(abs(refp))),max(max(abs(ref))));
    sprintf('%d , diff1 = %g, frac1 = %g, conv = %g\n', i, full(diff1), full(frac1(i)), full(conv(i)))
    old = new;
    new = 3 - old;
    if ( conv(i) < tol ) break;
    end
end
G1 = M{old}*G0; 
G2 = M{old}*G0*V*G0;


