function xylims = plotlinregxl_XYLim(d, data, covmats)
% determine x-y limits for multiple-dimensional regression data
% places first variable on x-axis, all others as vertical axes
% each column of xylim is the min and max for that variable

%ellipse parameters
pts = 300;
pis = 0:2*pi/(pts-1):2*pi;
circlepts = 2*[cos(pis') sin(pis')];   %a 2-column matrix of the points in a circle

npoints = size(data,1);
%nplots = d-1;
%xylims = zeros(2, d); %columns of data to fit code for _EnvelopeUnctMinMax

%initialize xylims to out-of-range negative and positive mins/maxes
xylims = repmat([10^10;10^-10],1,d); %2 rows, d columns

%first, plot first variable vs. second variable, get x-y limits
for i = 1:npoints
    s = covmats(:,:,i); sub_s = s([1 2], [1 2]);
    sc = chol(sub_s);
    elpts = circlepts*sc + repmat([data(i,1) data(i,2)], pts, 1);
    xylims(1,1) = min(xylims(1,1), min(elpts(:,1))); % xmin (where x is first variable)
    xylims(2,1) = max(xylims(2,1), max(elpts(:,1))); % xmax
    xylims(1,2) = min(xylims(1,2), min(elpts(:,2))); % ymin (where y is (i+1)st variable)
    xylims(2,2) = max(xylims(2,2), max(elpts(:,2))); % ymax
end

% for other plots, if d>2
for ploti = 3:d
    
    for i = 1:npoints
        s = covmats(:,:,i); sub_s = s([1 ploti], [1 ploti]);
        sc = chol(sub_s);
        elpts = circlepts*sc + repmat([data(i,1) data(i,ploti)], pts, 1);
        xylims(1,ploti) = min(xylims(1,ploti), min(elpts(:,2))); % ymin
        xylims(2,ploti) = max(xylims(2,ploti), max(elpts(:,2))); % ymax
    end

end


