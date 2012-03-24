package com.binaryme.ScratchTab.Gui.Shapes.Dummies;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Path;
import android.util.Log;

import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Blocks.Block.BlockType;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;
import com.binaryme.tools.ColorPalette;


public class ShapeSlotDummyBoolean extends ShapeSlotDummy {

	
	public ShapeSlotDummyBoolean(Activity context, Block<? extends Shape> associatedBlock) {
		super(context,associatedBlock);
	}


	
//DRAWING METHODS
	
	private Path drawSlotBoolean() {
		//draw a new Path
		Path path = new Path();
		path.moveTo(0, booleanSlotHalfHeight);
		
		path.rLineTo(booleanSlotPike, -booleanSlotHalfHeight);		//up and right
		path.rLineTo(booleanSlotMinWidth,0 );							//right
		path.rLineTo(booleanSlotPike, booleanSlotHalfHeight); 		//down and right
		path.rLineTo(-booleanSlotPike, booleanSlotHalfHeight); 		//down and left
		path.rLineTo(-booleanSlotMinWidth, 0);							//left
		path.rLineTo(-booleanSlotPike, -booleanSlotHalfHeight);		//up left
		
		path.close();
		return path;
	}

//IMPLEMENT HOOKS
	
	//Paths
	
	@Override
	public BlockType getType() {
		return BlockType.BOOLEAN;
	}

	@Override
	public Path drawPathEmpty() {
		return drawSlotBoolean();
	}

	@Override
	public Path drawPathActive() {
		return drawSlotBoolean();
	}

	@Override
	public Path drawPathHover() {
		return drawSlotBoolean();
	}

	//Colors
	
	@Override
	public int fillColorEmpty() {
		int color = ColorPalette.getCurrentBodyColor(this);
		Log.d("bodycolor","Bool dummy Root bodycolor "+color);
		return ColorPalette.makeDarker(color, 0.2f);
	}
	@Override
	public int strokeColorEmpty() {
		return Color.TRANSPARENT;
	}
	
	//TODO: move the following methods to the ShapeWithSlots, so that those colors are common for all slots?
	@Override
	public int fillColorActive() {
		return ColorPalette.makeBrider(fillColorEmpty(), 0.3f );
	}
	@Override
	public int strokeColorActive() {
		return Color.TRANSPARENT;
	}
	@Override
	public int fillColorHover() {
		return ColorPalette.colorSlotHoverFill;
	}
	@Override
	public int strokeColorHover() {
		return ColorPalette.colorSlotHoverStroke;
	}

}
