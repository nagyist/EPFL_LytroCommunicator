function FigureHandle = LFEdgeDetection( LF, detectionAlgorithme, smoothAlgorithme, varargin )

%---Defaults---
MouseRateDivider = 30;

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

%---Setup the display---
[ImageHandle,FigureHandle] = LFDispSetup( squeeze(LF(floor(end/2),floor(end/2),:,:,:)), varargin{:} );
colormap(gray(256));

BDH = @(varargin) ButtonDownCallback(FigureHandle, varargin);
BUH = @(varargin) ButtonUpCallback(FigureHandle, varargin);
set(FigureHandle, 'WindowButtonDownFcn', BDH );
set(FigureHandle, 'WindowButtonUpFcn', BUH );


%---Defaults---

switch detectionAlgorithme
    case 'sobel'
    case 'canny'
    otherwise
        detectionAlgorithme = 'canny';
end

detectionAlgorithme
smoothAlgorithme

[TSize,SSize, ~,~] = size(LF(:,:,:,:,1));
CurX = floor(SSize/2);
CurY = floor(TSize/2);
DragStart = 0;

%---Update frame before first mouse drag---
GrayLF = rgb2gray( squeeze( LF(round(CurY), round(CurX),:,:,:) ) );
LFRender = edge( GrayLF, detectionAlgorithme ) * 256;

set(ImageHandle,'cdata', LFRender);

fprintf('Click and drag to shift perspective\n');

function ButtonDownCallback(FigureHandle,varargin) 
set(FigureHandle, 'WindowButtonMotionFcn', @ButtonMotionCallback);
DragStart = get(gca,'CurrentPoint')';
DragStart = DragStart(1:2,1)';
end

function ButtonUpCallback(FigureHandle, varargin) 
set(FigureHandle, 'WindowButtonMotionFcn', '');
end

function ButtonMotionCallback(varargin) 
    CurPoint = get(gca,'CurrentPoint');
    CurPoint = CurPoint(1,1:2);
    RelPoint = CurPoint - DragStart;
    CurX = max(1,min(SSize, CurX - RelPoint(1)/MouseRateDivider));
    CurY = max(1,min(TSize, CurY - RelPoint(2)/MouseRateDivider));
    DragStart = CurPoint;

    GrayLF = rgb2gray( squeeze( LF(round(CurY), round(CurX),:,:,:) ) );

    switch smoothAlgorithme
        case 'average'
            LFRender = edge( filter2(fspecial('average',3),GrayLF), detectionAlgorithme ) * 256;
        case 'median'
            LFRender = edge( medfilt2(GrayLF,[3 3]), detectionAlgorithme ) * 256;
        case 'wiener'
            LFRender = edge( wiener2(GrayLF,[5 5]), detectionAlgorithme ) * 256;
        case 'gaussian'
            myfilter = fspecial('gaussian',[3 3], 0.5);
            LFRender = edge( imfilter(GrayLF, myfilter), detectionAlgorithme ) * 256;
        case 'none'
            LFRender = edge( GrayLF, detectionAlgorithme ) * 256; 
        otherwise
            LFRender = edge( GrayLF, detectionAlgorithme ) * 256; 
    end


    set(ImageHandle,'cdata', LFRender);
end

end