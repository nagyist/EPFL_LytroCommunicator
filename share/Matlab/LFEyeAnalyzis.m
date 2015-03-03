function FigureHandle = LFEyeAnalyzis( LF )

originalImage = LF;


%---Check for mono and clip off the weight channel if present---
Mono = (ndims(LF) == 4);
if( ~Mono )
    LF = LF(:,:,:,:,1:3);
end

%---Rescale for 8-bit display---
if( isfloat(LF) )
    LF = uint8(LF ./ max(LF(:)) .* 255);
else
    LF = uint8(LF.*(255 / double(intmax(class(LF)))));
end



%---Defaults---
scale = 2;
margin = 20*scale;
slice = 9*scale;
mainSize = 380*scale;

[TSize,SSize, XSize,YSize] = size(LF(:,:,:,:,1));
LensX = floor(TSize/2);
LensY = floor(SSize/2);
imX = floor(XSize/2);
imY = floor(YSize/2);




%---Setup the display---
fig = figure;
set(fig, 'Position', [200 200 449*scale 449*scale]);

axesLeft = axes('units', 'pixels', 'position', [margin,margin,slice,mainSize]);
axesTop = axes('units', 'pixels', 'position', [2 * margin + slice,2 * margin + mainSize, mainSize, slice]);
axesCenter = axes('units', 'pixels', 'position', [2*margin + slice, margin,mainSize,mainSize]);

TopImageHandle = imshow(squeeze(LF(LensX,:,imX,:,:)), 'Parent', axesTop);
LeftImageHandle = imshow(permute(squeeze(LF(:,LensY,:,imY,:)), [2,1,3]), 'Parent', axesLeft);
CenterImageHandle = imshow(squeeze(LF(LensX,LensY,:,:,:)), 'Parent', axesCenter);




%---Set callback to main image---
set(CenterImageHandle,'ButtonDownFcn',@ImageClickCallback);
set(TopImageHandle,'ButtonDownFcn',@TopSliceClickCallback);
set(LeftImageHandle,'ButtonDownFcn',@LeftSliceClickCallback);



function ImageClickCallback ( objectHandle , eventData )
	axesHandle  = get(objectHandle,'Parent');
	coordinates = get(axesHandle,'CurrentPoint'); 
	coordinates = coordinates(1,1:2);
	imY = ceil(coordinates(1));
	imX = ceil(coordinates(2));
	set(LeftImageHandle, 'CData', permute(squeeze(LF(:,LensY,:,imY,:)), [2,1,3]) );
	set(TopImageHandle, 'CData', squeeze(LF(LensX,:,imX,:,:)) );
end




function TopSliceClickCallback ( objectHandle, eventData )
    axesHandle  = get(objectHandle,'Parent');
    coordinates = get(axesHandle,'CurrentPoint'); 
    coordinates = coordinates(1,1:2);
    LensX = ceil(coordinates(2));
    set(TopImageHandle, 'CData', squeeze(LF(LensX,:,imX,:,:)) );
end




function LeftSliceClickCallback ( objectHandle, eventData )
    axesHandle  = get(objectHandle,'Parent');
    coordinates = get(axesHandle,'CurrentPoint'); 
    coordinates = coordinates(1,1:2);
    LensY = ceil(coordinates(1));
    set(LeftImageHandle, 'CData', permute(squeeze(LF(:,LensY,:,imY,:)), [2,1,3]) );
end

end