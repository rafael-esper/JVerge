package domain;

import java.net.MalformedURLException;
import java.net.URL;

public class MapUtil {

	public static void main(String[] args) throws MalformedURLException {
		//resizeRotine();
		//adjustRotine();
		//copyRotine();
		rotateRotine();
	}
	
	private static void rotateRotine() throws MalformedURLException {
		/*Map d = new Map(new URL("file:/C:\\javaref\\eclipse_workspace\\jgame-sample_simplified1\\src\\ps\\palma.map"));
		
		int temp = d.startX;
		d.startX = d.startY;
		d.startY = temp;
		
		d.zoneLayer = two_into_one(rotateMatrixLeft(one_into_two(d.zoneLayer, d.layers[0].width)));
		//d.zoneLayer = two_into_one(rotateMatrixLeft(one_into_two(d.zoneLayer, d.layers[0].height)));
		for(int l=0; l<d.layers.length; l++) {
			d.layers[l].tiledata = two_into_one(rotateMatrixLeft(one_into_two(d.layers[l].tiledata, d.layers[l].width)));
			//d.layers[l].tiledata = two_into_one(rotateMatrixLeft(one_into_two(d.layers[l].tiledata, d.layers[l].height)));
			
			temp = d.layers[l].height;
			d.layers[l].height = d.layers[l].width;
			d.layers[l].width = temp;
		}
		
		d.save("C:\\Palma_rot.map");
*/
		
		/* Test functions*/
 		int[] a = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
		int[][] b1 = one_into_two(a, 3);
		int[][] b2 = one_into_two(a, 2);
		int[][] b3 = one_into_two(a, 4);
		int[][] b4 = one_into_two(a, 6);
		int[] c1 = two_into_one(b1);
		int[] c2 = two_into_one(b2);
		int[] c3 = two_into_one(b3);
		int[] c4 = two_into_one(b4);
		
		System.out.println("Debug here!");
		

		 /**/
	}
	
	public static int [] two_into_one(int[][] matrix) {
		int[] ret = new int[matrix.length * matrix[0].length];
		
		int pos = 0;
		for(int j=0;j<matrix.length; j++) {
			for(int i=0;i<matrix[j].length; i++) {
				ret[pos++] = matrix[j][i];
			}
		}
		return ret;
	}
	
	public static int[][] one_into_two (int[] matrix, int width) {
		int height = matrix.length / width;
		int[][] ret = new int[height][width];
		System.out.println("Esse é: " + height + " x " + width);
		for(int i=0; i<matrix.length; i++) { 
			ret[i/width][i%width] = matrix[i];
		}
		return ret;
	}

	static int [] dimensionar_duas_em_uma(int[][] matriz) {
		int[] ret = new int[matriz.length * matriz[0].length];
		
		int pos = 0;
		for(int j=0;j<matriz.length; j++) {
			for(int i=0;i<matriz[j].length; i++) {
				ret[pos++] = matriz[j][i];
			}
		}
		return ret;
	}
	
	static int[][] dimensionar_uma_em_duas  (int[] matriz, int largura) {
		int altura = matriz.length / largura;
		int[][] ret = new int[altura][largura];
		for(int i=0; i<matriz.length; i++) { 
			ret[i/largura][i%largura] = matriz[i];
		}
		return ret;
	}
	
	
	public static int[][] rotateMatrixRight(int[][] matrix)
	{
	    int width = matrix.length;
	    int height = matrix[0].length;
	    int[][] ret = new int[height][width];
	    for (int i=0; i<height; i++) {
	        for (int j=0; j<width; j++) {
	            ret[i][j] = matrix[width - j - 1][i];
	        }
	    }
	    return ret;
	}
	public static int[][] rotateMatrixLeft(int[][] matrix)
	{
	    int width = matrix.length;
	    int height = matrix[0].length;   
	    int[][] ret = new int[height][width];
	    for (int i=0; i<height; i++) {
	        for (int j=0; j<width; j++) {
	            ret[i][j] = matrix[j][height - i - 1];
	        }
	    }
	    return ret;
	}
	
		
	private static void resizeRotine() {
		//path = "C:\\RBP\\RPG\\PS\\PS";
		// String strFilePath = "C:\\TESTE.MAP";

		Map d = new Map("Palma.map");
	
		for(int j = 0; j<108; j++) {
			for(int i = 0; i<139; i++) {
				d.SetZone(i, j, d.zone(i+7, j+9));
				for(int l=0; l<d.layers.length; l++) {
					d.layers[l].SetTile(i, j, d.layers[l].GetTile(i+7, j+9));
					d.SetObs(i,j,0);
					System.out.print(" " + d.layers[l].GetTile(i, j));
				}
				/*
				if(d.layers[0].GetTile(i, j) >=162 && d.layers[0].GetTile(i, j) <= 167)
					d.SetObs(i, j, 1);
				if(d.layers[0].GetTile(i, j) >=180 && d.layers[0].GetTile(i, j) <= 185)
					d.SetObs(i, j, 1);
				if(d.layers[0].GetTile(i, j) ==178 ||
						d.layers[0].GetTile(i, j) ==178 ||
						d.layers[0].GetTile(i, j) ==179 ||
						d.layers[0].GetTile(i, j) ==196 ||
						d.layers[0].GetTile(i, j) ==197 ||
						d.layers[0].GetTile(i, j) ==214 ||
						d.layers[0].GetTile(i, j) ==215 ||
						d.layers[0].GetTile(i, j) ==232 ||
						d.layers[0].GetTile(i, j) ==233 ||
						d.layers[0].GetTile(i, j) ==306 ||
						d.layers[0].GetTile(i, j) ==307 ||
						d.layers[0].GetTile(i, j) ==324 ||
						d.layers[0].GetTile(i, j) ==325 ||
						d.layers[0].GetTile(i, j) ==126 ||
						d.layers[0].GetTile(i, j) ==342 ||
						d.layers[0].GetTile(i, j) ==343)
					d.SetObs(i, j, 1);
				//if(d.obstruct(i+7, j+9))
//					d.SetObs(i, j, 1);*/
			}
		}
		
		System.out.println(d.getHeight() + " \\" + d.getWidth() + "\t" + d.layers.length + "\t" + d.layers[0].tiledata.length + "\t" + d.zoneLayer.length);
		d.save("C:\\Palma.map");			
	}
		
	private static void adjustRotine() {
		//path = "C:";
		Map d = new Map("Palma.map");
	
		int WATER = 245;
		int GRASS = 131;
		int SAND = 244;
		for(int j = 0; j<d.getHeight()-3; j++) {
			for(int i = 0; i<d.getWidth()-3; i++) {
				if(d.layers[0].GetTile(i, j) == d.layers[0].GetTile(i, j+1) &&
				   d.layers[0].GetTile(i, j) == d.layers[0].GetTile(i, j+2) &&
				   d.layers[0].GetTile(i, j) == 513) {
						d.layers[0].SetTile(i, j, GRASS);
						d.layers[0].SetTile(i, j+1, GRASS);
				}
					
			}
		}		
		System.out.println(d.getHeight() + " \\" + d.getWidth() + "\t" + d.layers.length + "\t" + d.layers[0].tiledata.length + "\t" + d.zoneLayer.length);
		d.save("C:\\Palma.map");		
	}
	
	
	/* Copies content from one map M to a map D
	 */
	private static void copyRotine() {
		//path = "C:\\RBP\\RPG\\PS\\PS";
		// String strFilePath = "C:\\TESTE.MAP";

		Map m = new Map("Palma.map");
		
		//path = "C:";
		Map d = new Map("Palma.map");
	
		int posi = 300; int posj = 62;
		for(int j = 25; j<35; j++) {
			for(int i = 107; i<117; i++) {
				for(int l=0; l<d.layers.length; l++) {
					d.layers[l].SetTile(posi, posj, m.layers[l].GetTile(i, j));
					System.out.print(" " + d.layers[l].GetTile(i, j));
				}
				posi++;
			}
			posj++;posi = 300;
		}		
		System.out.println(d.getHeight() + " \\" + d.getWidth() + "\t" + d.layers.length + "\t" + d.layers[0].tiledata.length + "\t" + d.zoneLayer.length);
		d.save("C:\\Palma.map");		
	}

}
