function [G,conv] = itermatinvG(G0,V,tol,maxit)
% Compute graph kernel G 
% G = (I - G0*V)^(-1)*G0
%   = G0 + G0*V*G0 + G0*V*G0*V*G0 + G0*V*G0*V*G0*V*G0 + ...;
%   = (I + G0*V + G0*V*G0*V + G0*V*G0*V*G0*V + ...)*G0;
% M(0) = I ; M(1) = I + G0*V; M(2) = I + G0*V + G0*V*G0*V;
% M(k+1) = I + G0*V*M(k); k = 0,1,...
% G = M(end)*G0;

nv = size(G0,1);
H = G0*V;
sprintf('starting iterations\n')
% initial M = I; 
I = eye(nv);
M = cell(2,1);
M{1} = I;
M{2} = zeros(nv);
old = 1;
new = 2;
ref = I - H;
frac1 = zeros(1,maxit);
conv = zeros(1,maxit);

for i = 1:maxit
    M{new} = I + H * M{old};
    diff1 = sum( sum(abs(M{new} - M{old}) )) / (nv * nv); % mean diff for element
    mval1 = max(max(abs(M{new})));
    frac1(i) = diff1 / mval1;      % aver diff/ max matrix element
    
    % If ref = (I - H) is rank deficient, 
    % M is the pseudo-inverse of ref, i.e. ref*M*ref = ref
    refp = ref*M{new}*ref;
    pseudoinvdiff = max(max(abs(refp-ref)));
    conv(i) = pseudoinvdiff/max(max(max(abs(refp))),max(max(abs(ref))));
    sprintf('%d , diff1 = %g, frac1 = %g, conv = %g\n', i, full(diff1), full(frac1(i)), full(conv(i)))
    old = new;
    new = 3 - old;
    if ( conv(i) < tol ) break;
    end
end

if ( conv(end) > tol )
    disp(['Did not converge within ' num2str(maxit) ' iterations!']);
else
    disp(['Converged within ' num2str(i) 'iterations!']);
end

G = M{old}*G0;


