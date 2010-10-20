function [rocRes] = changeScoreHypoTest(score, trH0, trH1, matFile, trace2, score2, nW2);
if nargin > 4 
    assert(length(trace2) == length(score2) , 'score and trace must have same length');
    result = struct('traceVec', trace2, 'scoreVec', score2);  
    N = length(trace2); 
    nW = 5; 
else
    N = 1000; 
    if nargin < 4
        load('result2hqt');
    else
        load(matFile); 
    end
end 

if nargin <= 2
    trH1 = 50;
    trH0 = 50; 
    if nargin < 1
        score = nW * 0.5; 
    end
end
assert(trH0 <= trH1, 'Enter lower trace first.'); 
classVec = zeros(N, 1); % -1=> H0 +1=>H1 (ignore in between)
classVec = classVec - (result.traceVec > (trH1 * nW / 100.0) ); 
classVec = classVec + (result.traceVec < (trH0 * nW / 100.0) );
predVec = zeros(N, 1);
rocMat = zeros(2, 2);
for n=1:N
    cs = result.scoreVec(n); 
    if(cs >= score)
        predVec(n) = 1;
    end
    if ( (predVec(n) == 1) && (classVec(n) == 1))
        rocMat(1, 1) = rocMat(1, 1) + 1;
    elseif ( (predVec(n) == 1) && (classVec(n) == -1))
        rocMat(1, 2) = rocMat(1, 2) + 1;
    elseif ( (predVec(n) == 0) && (classVec(n) == 1))
        rocMat(2, 1) = rocMat(2, 1) + 1;
    elseif( (predVec(n) == 0) && (classVec(n) == -1))
        rocMat(2, 2) = rocMat(2, 2) + 1;
    end
end  
tpr = rocMat(1, 1) * 1.0/ ( rocMat(1, 1) + rocMat(2, 1)); 
fpr = rocMat(1, 2) * 1.0/ ( rocMat(1, 2) + rocMat(2, 2)); 
rocRes = struct('tpr', tpr, 'fpr', fpr, 'rocMat', rocMat, ...
    'score', score, 'trH', [trH0 trH1]); 
