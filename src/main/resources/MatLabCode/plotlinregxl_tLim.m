function tminmax = plotlinregxl_tLim(a, v, xylims)
%find limits for parameteric parameter t in linear regression plots

d = length(a);
numplots = d-1;
tminmax = zeros(numplots,2);% tmax = zeros(4,nruns);

xlims = xylims(:,1);  %the same for every plot
txlims = (xlims-a(1))/v(1);
for ploti = 1:numplots
    yvar = ploti + 1;   %for the first plot, 2nd variable is on y-axis, etc.
    ylimi = xylims(:,yvar);
    yl = a(yvar)+v(yvar)*txlims(1);  %left-most possible y value
    yr = a(yvar)+v(yvar)*txlims(2);  %right-most possible y value
    
    if v(1)*v(yvar) > 0  % positive slope
        if yl > ylimi(1)
            tminmax(ploti, 1) = (xlims(1)-a(1))/v(1);       %min
        else
            tminmax(ploti, 1) = (ylimi(1)-a(yvar))/v(yvar); %min
        end
        if yr > ylimi(2)
            tminmax(ploti, 2) = (ylimi(2)-a(yvar))/v(yvar); %max
        else
            tminmax(ploti, 2) = (xlims(2)-a(1))/v(1);       %max
        end
    else  %negative slope
        if yl < ylimi(2)
            tminmax(ploti, 1) = (xlims(1)-a(1))/v(1);       %min
        else
            tminmax(ploti, 1) = (ylimi(2)-a(yvar))/v(yvar); %min
        end
        if yr > ylimi(1)
            tminmax(ploti, 2) = (xlims(2)-a(1))/v(1);       %max
        else
            tminmax(ploti, 2) = (ylimi(1)-a(yvar))/v(yvar); %max
        end
    end
    
end







% for runi = 1:nruns
% 
%     
%     xlimi = xylims(:,5,runcount)';
% 
%         %202/206 vs. 208/206
%         ylimi = xylims(:,1,runcount)'; 
%         txlim = (xlimi-a(5))/v(5); yl = a(1)+v(1)*txlim(1); yr = a(1)+v(1)*txlim(2);
%         if v(1) > 0 %positive slope
%             if yl > ylimi(1)
%                 tmin(1,runcount) = (xlimi(1)-a(5))/v(5);
%             else
%                 tmin(1,runcount) = (ylimi(1)-a(1))/v(1);
%             end
%             if yr > ylimi(2)
%                 tmax(1,runcount) = (ylimi(2)-a(1))/v(1);
%             else
%                 tmax(1,runcount) = (xlimi(2)-a(5))/v(5);
%             end
%         else  %negative slope
%             if yl < ylimi(2)
%                 tmin(1,runcount) = (xlimi(1)-a(5))/v(5);
%             else
%                 tmin(1,runcount) = (ylimi(2)-a(1))/v(1);
%             end
%             if yr > ylimi(1)
%                 tmax(1,runcount) = (xlimi(2)-a(5))/v(5);
%             else
%                 tmax(1,runcount) = (ylimi(1)-a(1))/v(1);
%             end
%         end
%         
%         %204/206 vs. 208/206
%         ylimi =  xylims(:,2,runcount)'; 
%         txlim = (xlimi-a(5))/v(5); yl = a(2)+v(2)*txlim(1); yr = a(2)+v(2)*txlim(2);
%         if v(2) > 0 %positive slope
%             if yl > ylimi(1)
%                 tmin(2,runcount) = (xlimi(1)-a(5))/v(5);
%             else
%                 tmin(2,runcount) = (ylimi(1)-a(2))/v(2);
%             end
%             if yr > ylimi(2)
%                 tmax(2,runcount) = (ylimi(2)-a(2))/v(2);
%             else
%                 tmax(2,runcount) = (xlimi(2)-a(5))/v(5);
%             end
%         else  %negative slope
%             if yl < ylimi(2)
%                 tmin(2,runcount) = (xlimi(1)-a(5))/v(5);
%             else
%                 tmin(2,runcount) = (ylimi(2)-a(2))/v(2);
%             end
%             if yr > ylimi(1)
%                 tmax(2,runcount) = (xlimi(2)-a(5))/v(5);
%             else
%                 tmax(2,runcount) = (ylimi(1)-a(2))/v(2);
%             end
%         end
% 
%         
%         %205/206 vs. 208/206
%         ylimi =  xylims(:,3,runcount)'; 
%         txlim = (xlimi-a(5))/v(5); yl = a(3)+v(3)*txlim(1); yr = a(3)+v(3)*txlim(2);
%         if v(3) > 0 %positive slope
%             if yl > ylimi(1)
%                 tmin(3,runcount) = (xlimi(1)-a(5))/v(5);
%             else
%                 tmin(3,runcount) = (ylimi(1)-a(3))/v(3);
%             end
%             if yr > ylimi(2)
%                 tmax(3,runcount) = (ylimi(2)-a(3))/v(3);
%             else
%                 tmax(3,runcount) = (xlimi(2)-a(5))/v(5);
%             end
%         else   %negative slope
%             if yl < ylimi(2)
%                 tmin(3,runcount) = (xlimi(1)-a(5))/v(5);
%             else
%                 tmin(3,runcount) = (ylimi(2)-a(3))/v(3);
%             end
%             if yr > ylimi(1)
%                 tmax(3,runcount) = (xlimi(2)-a(5))/v(5);
%             else
%                 tmax(3,runcount) = (ylimi(1)-a(3))/v(3);
%             end
%         end
%         
%         %207/206 vs. 208/206
%         ylimi =  xylims(:,4,runcount)'; 
%         txlim = (xlimi-a(5))/v(5); yl = a(4)+v(4)*txlim(1); yr = a(4)+v(4)*txlim(2);
%         if v(4) > 0 %positive slope
%             if yl > ylimi(1)
%                 tmin(4,runcount) = (xlimi(1)-a(5))/v(5);
%             else
%                 tmin(4,runcount) = (ylimi(1)-a(4))/v(4);
%             end
%             if yr > ylimi(2)
%                 tmax(4,runcount) = (ylimi(2)-a(4))/v(4);
%             else
%                 tmax(4,runcount) = (xlimi(2)-a(5))/v(5);
%             end
%         else %negative slope
%             if yl < ylimi(2)
%                 tmin(4,runcount) = (xlimi(1)-a(5))/v(5);
%             else
%                 tmin(4,runcount) = (ylimi(2)-a(4))/v(4);
%             end
%             if yr > ylimi(1)
%                 tmax(4,runcount) = (xlimi(2)-a(5))/v(5);
%             else
%                 tmax(4,runcount) = (ylimi(1)-a(4))/v(4);
%             end
%         end
% 
%     runcount = runcount + 1;
% end
