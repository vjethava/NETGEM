%%% variation wrt time
T = [5 10 20 50];
RT  = [];
NUM = 20;
F = zeros(NUM, length(T), 3); 
D = zeros(NUM, length(T), 3); 
for i=1:length(T)
    t = T(i);
    for j=1:NUM
        R = exp_mm(t); 
        for k=1:3
            n = R(k).n; 
            F(j, i, k) = R(k).fscore(n); 
            D(j, i, k) = R(k).fdiff(n); 
    %        save mm_tvar.mat; 
        end
    end
    RT = [RT; R];
end
nT = length(T); 
mF = reshape(mean(F, 1), nT, 3);
vF = reshape(var(F, [], 1), nT, 3);
subplot(2, 1, 1); 
errorbar(T'*ones(1, 3), mF, vF, '-*', 'LineWidth', 2);
legend('Large HMM', 'Factorial HMM', 'Mixture Model'); 
title('F-score vs length of sequence'); 
xlabel('time'); 
ylabel('f-score');
subplot(2, 1, 2); 
mD = reshape(mean(D, 1), nT, 3);
vD = reshape(var(D, [], 1), nT, 3);
errorbar(T'*ones(1, 3), mD, vD, '-*', 'LineWidth', 2);
title('Frobenius norm between true Q and estimated Q'); 
legend('Large HMM', 'Factorial HMM', 'Mixture Model'); 
xlabel('time'); 
ylabel('f-norm');