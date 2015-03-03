

function Display( LF )
    %---Check for mono and clip off the weight channel if present---
    Mono = (ndims(LF) == 4);
    if( ~Mono )
        LF = LF(:,:,:,:,1:3);
    end

    % if( isfloat(LF) )
    %     LF = uint8(LF ./ max(LF(:)) .* 255);
    % else
    %     LF = uint8(LF.*(255 / double(intmax(class(LF)))));
    % end

    spacement=50;
    U=5;
    V=5;
    I=50;
    J=50;

    h=spacement+size(LF,1);
    

    viewFull = drawFullView();
    imageHandle = imshow(viewFull);

    set(imageHandle, 'ButtonDownFcn', @ImageClickCallback);

    function viewFull = drawFullView()

        centerView=squeeze(LF(U, V, :, :, :));

        % message= sprintf('u: %.1f , j: %.1f',pU, pJ);
        % disp(message);
        leftView=squeeze(LF(:, V, :, J, :));
        % leftView=expand(leftView, 2);
        leftView=imrotate(leftView, -90);

        % size(leftView)
        % leftView=reshape(leftView, size(leftView, 2), size(leftView, 1), 3);


        topView=squeeze(LF(U, :, I, :, :));

        viewFull=ones(size(centerView, 1) + h, size(centerView, 2) + h, 3) .* (.8 * 65536);
                
        viewFull(h+1:end, h+1:end, :)=centerView;
        viewFull(h+1:end, 1:size(leftView, 2), :)=leftView;
        viewFull(1:size(topView, 1), h+1:end, :)=topView;

        viewFull=viewFull ./ 65536;

        viewFull=drawMarker(viewFull, h+I, h+J, [1 0 0]);
        viewFull=drawMarker(viewFull, U, h+(size(centerView, 1)/2), [0 1 0]);
        viewFull=drawMarker(viewFull, h+(size(centerView, 2)/2), V, [0 1 0]);
    end

    function viewFull = drawMarker(viewFull, mI, mJ, color)
        left=max(mJ-2, 1);
        top=max(mI-2, 1);

        right=min(mJ+2, size(viewFull, 2));
        bottom=min(mI+2, size(viewFull, 1));
        
        

        % bottom-top
        % right-left
        % size(repmat(color, bottom-top+1, right-left+1))
        % size(viewFull(top:bottom, left:right, :))
        for ti=top:bottom
            for tj=left:right
                viewFull(ti, tj, :)=color;
            end
        end
        % viewFull(top:bottom, left:right, :)=repmat(color, bottom-top+1, right-left+1)
    end

    % function view = expand(view, count)
    %     s = size(view);
    %     tmp = repmat(view, 1, count);        
    %     view = reshape(tmp, s(1)*count, s(2), 3);
    %     % view = reshape(repmat(view, 1, count), s(1)*count, s(2));
    % end

    function ImageClickCallback(objectHandle, eventData)
        axesHandle  = get(objectHandle,'Parent');
        coordinates = get(axesHandle,'CurrentPoint');
        x=round(coordinates(1, 1));
        y=round(coordinates(1, 2));
        % coordinates = coordinates(1,1:2);
        % message= sprintf('x: %.1f , y: %.1f',j, i);
        % disp(message);

        if (x < size(LF,1)+1) && (y > h)  % LEFT VIEW
            V=x;
        elseif (x > h) && (y < size(LF,1)+1) % TOP VIEW
            U=y;
        elseif (x > h) && (y > h) % CENTER VIEW

            I=y-h;
            J=x-h;

            
        end

        fullView = drawFullView();
        set(imageHandle, 'cdata', fullView);

    end    
end