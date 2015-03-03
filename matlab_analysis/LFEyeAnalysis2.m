function FigureHandle = LFEyeAnalysis2( LF )

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

[TSize,SSize, XSize,YSize] = size(LF(:,:,:,:,1));

algorithme = 'canny';

scale = 2;
margin = 20*scale;
slice = TSize*scale;
mainSize = XSize*scale;


LensX = floor(TSize/2);
LensY = floor(SSize/2);
imX = floor(XSize/2);
imY = floor(YSize/2);

figureSize = 4*margin + 2 * slice + mainSize;


contrast = imadjust(squeeze(LF(5,5,:,:,:)),[0.1 0.1 0.1; 0.3 0.3 0.3],[0 0 0; 1 1 1]);

imshow(contrast);

%contrast = imsharpen(contrast);

%imshow(contrast);


%---Setup the display---
fig = figure;
set(fig, 'Position', [200 200 figureSize figureSize]);

%-- depth ---
axesLeft1 = axes('units', 'pixels', 'position', [margin,margin,slice,mainSize]);
axesTop1 = axes('units', 'pixels', 'position', [3 * margin + 2 * slice,3 * margin + mainSize + slice , mainSize, slice]);

%-- edge detection ----
axesLeft2 = axes('units', 'pixels', 'position', [margin*2 + slice,margin,slice,mainSize]);
axesTop2 = axes('units', 'pixels', 'position', [3 * margin + 2 * slice,2 * margin + mainSize, mainSize, slice]);

axesCenter = axes('units', 'pixels', 'position', [3*margin + 2 * slice, margin,mainSize,mainSize]);

TopImageHandle1 = imshow(squeeze(LF(LensX,:,imX,:,:)), 'Parent', axesTop1);
LeftImageHandle1 = imshow(permute(squeeze(LF(:,LensY,:,imY,:)), [2,1,3]), 'Parent', axesLeft1);
TopImageHandle2 = imshow(edge(rgb2gray(squeeze(LF(LensX,:,imX,:,:))), algorithme), 'Parent', axesTop2);
LeftImageHandle2 = imshow(permute(edge(rgb2gray(squeeze(LF(:,LensY,:,imY,:))), algorithme), [2,1,3]), 'Parent', axesLeft2);
CenterImageHandle = imshow(squeeze(LF(LensX,LensY,:,:,:)), 'Parent', axesCenter);




%---Set callback to main image---
set(CenterImageHandle,'ButtonDownFcn',@ImageClickCallback);
%set(TopImageHandle,'ButtonDownFcn',@TopSliceClickCallback);
%set(LeftImageHandle,'ButtonDownFcn',@LeftSliceClickCallback);



function ImageClickCallback ( objectHandle , eventData )
	axesHandle  = get(objectHandle,'Parent');
	coordinates = get(axesHandle,'CurrentPoint'); 
	coordinates = coordinates(1,1:2);
	imY = ceil(coordinates(1));
	imX = ceil(coordinates(2));
    set(LeftImageHandle1, 'CData', permute(squeeze(LF(:,LensY,:,imY,:)), [2,1,3]) );
    set(TopImageHandle1, 'CData', squeeze(LF(LensX,:,imX,:,:)) );
	set(LeftImageHandle2, 'CData', permute(edge(rgb2gray(squeeze(LF(:,LensY,:,imY,:))), algorithme), [2,1,3]) );
	set(TopImageHandle2, 'CData', edge(rgb2gray(squeeze(LF(LensX,:,imX,:,:))),algorithme) );
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