%%% variation wrt number of strains
S = [1 2 3 4 5];
nS = length(S); 
nT = 10; 
RT  = [];
NUM = 20;
F = zeros(NUM, nS, 3); 
D = zeros(NUM, nS, 3); 
for s=1:nS
    for j=1:NUM
        cS = unidrnd(2, 1, S(s)) - 1; 
        R = exp_mm(nT, cS); 
        for k=1:3
            n = R(k).n; 
            F(j, s, k) = R(k).fscore(n); 
            D(j, s, k) = R(k).fdiff(n); 
            save mm_svar.mat; 
        end
    end
    RT = [RT; R];
end
nT = length(T); 
mF = reshape(mean(F, 1), nS, 3);
vF = reshape(var(F, [], 1), nS, 3);
subplot(2, 1, 1); 
errorbar(S'*ones(1, 3), mF, vF, '-*', 'LineWidth', 2);
legend('Large HMM', 'Factorial HMM', 'Mixture Model'); 
title('F-score vs length of sequence'); 
xlabel('strains'); 
ylabel('f-score');
subplot(2, 1, 2); 
mD = reshape(mean(D, 1), nS, 3);
vD = reshape(var(D, [], 1), nS, 3);
errorbar(S'*ones(1, 3), mD, vD, '-*', 'LineWidth', 2);
title('Frobenius norm between true Q and estimated Q'); 
legend('Large HMM', 'Factorial HMM', 'Mixture Model'); 
xlabel('strains'); 
ylabel('f-norm');