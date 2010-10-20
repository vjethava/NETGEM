clear; 
h = figure; 
hold on;
ll = get(gcf, 'CurrentAxes');
set(gca, 'FontSize', 15);
set(gcf, 'DefaultAxesColorOrder',[1 0 0;0 1 0;0 0 1], ...
     'DefaultAxesLineStyleOrder', '-*|-+|-o|-x|-s|-d|-p');
markers = {'b-*';'g-+';'r-o';'m-p';'c-s'}; 
thresholds = [5 5];
np1 = ceil(sqrt(size(thresholds, 1)));
np2 = np1 - 1;
if (np1*(np2) < size(thresholds, 1)) 
    np2 = np2 + 1;
end
legends = {}; 
for t = 1:size(thresholds,1)
stats = [];
fpr =[];
tpr =[];
tLow = thresholds(t, 1); 
tHigh = thresholds(t, 2); 
assert(tLow <= tHigh); 
cutoff = [0.0:0.2:6.0]'; 
for s=1:length(cutoff)
    cstat =  changeScoreHypoTest(cutoff(s), tLow, tHigh, 'qTrData/1836.mat');
    stats = [stats; cstat];
    fpr = [fpr; cstat.fpr]; 
    tpr = [tpr; cstat.tpr];
    
end
subplot(np1, np2, t);
plot(fpr, tpr, markers{t}, 'LineWidth', 3 , 'MarkerSize', 6); 
grid on; 
xlabel('False Positive Rate');
ylabel('True Positive Rate'); 
for s=1:length(cutoff)
    m = sprintf('%2.1f', cutoff(s)); 
    text(fpr(s)+0.005, tpr(s)-0.02, m,'HorizontalAlignment','left', 'FontSize', 6); 
end
legends{t} = sprintf('%d-%d', tLow, tHigh);
legend(legends{t}, 'Location', 'SouthEast');
end
% legend(legends, 'Location', 'SouthEast'); 
% xlabel('False Positive Rate');
% ylabel('True Positive Rate'); 
saveas(h, 'qTrH.fig');
print('-dpng', 'qTrH');
