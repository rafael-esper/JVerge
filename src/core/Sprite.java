package core;

import java.util.List;

import static core.VergeEngine.*;
import static core.Script.*;

import domain.VImage;

public class Sprite {

	int x, y;
	int sc;
	int ent = -1;
	VImage image, alphamap;
	int xflip, yflip;
	int addsub, lucent;
	int thinkrate;
	int thinkctr;
	int silhouette;
	int color;
	int wait;	// delay before processed.
	int timer;	// A timer of how long the sprite has been active.
	int layer;
	int onmap;
	String thinkproc;	


	/****************************** data ******************************/

	public static List<Sprite> sprites;

	
	/****************************** code ******************************/

	public static void ResetSprites()
	{
		if (!sprites.isEmpty())
			sprites.clear();
			//sprites.erase(sprites.begin(),sprites.end());

		/*
		for (int i=0; i<256; i++)
		{
			sprites.get(i) = sprite();
		}
		*/
	}

	public static int GetSprite()
	{
		int i;

		for (i=0; i<sprites.size(); i++)
		{
			if (sprites.get(i).image == null)
			{
				// Reset the element.
				sprites.set(i, new Sprite());
				return i;
			}
		}

		// rbp ?? sprites.push_back(sprite());
		return sprites.size() - 1;
	}

	public static void RenderSprite(int i)
	{
			/*rbp int zx, zy;
			if (sprites.get(i).image == null) return;
			if (sprites.get(i).wait > 0) return;
			zx = sprites.get(i).x;
			zy = sprites.get(i).y;
			if (sprites.get(i).sc==0 || sprites.get(i).ent >= 0)
			{
				zx -= xwin;
				zy -= ywin;
			}
			if (sprites.get(i).ent >= 0)
			{
				zx += entity[sprites.get(i).ent].getx() - entity[sprites.get(i).ent].chr.hx;
				zy += entity[sprites.get(i).ent].gety() - entity[sprites.get(i).ent].chr.hy;
			}
			VImage spr = ImageForHandle(sprites.get(i).image);
			if (sprites.get(i).alphamap)
			{
				VImage alphamap = ImageForHandle(sprites.get(i).alphamap);
				AlphaBlit(zx, zy, spr, alphamap, screen);
				return;
			}
			if (sprites.get(i).addsub !=0)
			{
				SetLucent(sprites.get(i).lucent);
				if (sprites.get(i).addsub<0)
					TSubtractiveBlit(zx, zy, spr, screen);
				else
					TAdditiveBlit(zx, zy, spr, screen);
				return;
			}
			if (sprites.get(i).silhouette!=0)
			{
				SetLucent(sprites.get(i).lucent);
				Silhouette(zx, zy, sprites.get(i).color, spr, screen);
				return;
			}
			SetLucent(sprites.get(i).lucent);
			TBlit(zx, zy, spr, screen);*/
	}

	public static void RenderSprites()
	{
		for (int i=0; i<sprites.size(); i++)
		{
			if (sprites.get(i).onmap!=0) continue;
			RenderSprite(i);
		}
		setlucent(0);
	}

	public static void RenderLayerSprites(int layer)
	{
		for (int i=0; i<sprites.size(); i++)
		{
			if (sprites.get(i).onmap==0) continue;
			if (sprites.get(i).ent >= 0) continue;
			if (sprites.get(i).layer != layer) continue;
			RenderSprite(i);
		}
		setlucent(0);
	}

	public static void RenderSpritesBelowEntity(int ent)
	{
		if(sprites == null) //rbp
			return;
		for (int i=0; i<sprites.size(); i++)
		{
			if (sprites.get(i).onmap==0) continue;
			if (sprites.get(i).ent != ent) continue;
			if (sprites.get(i).layer > 0) continue;
			RenderSprite(i);
		}
		setlucent(0);
	}


	public static void RenderSpritesAboveEntity(int ent)
	{
		if(sprites == null) //rbp
			return;
		for (int i=0; i<sprites.size(); i++)
		{
			if (sprites.get(i).onmap==0) continue;
			if (sprites.get(i).ent != ent) continue;
			if (sprites.get(i).layer < 1) continue;
			RenderSprite(i);
		}
		setlucent(0);
	}	
	
}
