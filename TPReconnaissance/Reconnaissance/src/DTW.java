import java.util.Vector;

import javafx.geometry.Point2D;


public class DTW {
	Vector<Point2D> userGesture;
	
	public DTW (Vector<Point2D> userGesture){
		this.userGesture = userGesture;
	}

	public Template detectGesture() {
		TemplateManager manager = new TemplateManager();
		manager.loadFile("gestures.xml");
		Vector<Template> theTemplates = manager.getTemplates();
		Template minTemp = null;
		double minValues = Double.MAX_VALUE;
		
		Vector<Point2D> userNorm = normalize(userGesture);
		
		for(Template tmpTemp : theTemplates){
			Matrix tmp = calculDtw(userNorm, normalize(tmpTemp.getPoints()));
			double tmpVal = tmp.items[tmp.nRows-1][tmp.nCols-1]; 
			if(tmpVal < minValues){
				minValues = tmpVal;
				minTemp = tmpTemp;
			}
		}
		
		
		
		
		return minTemp;
	}
	
	public Vector<Point2D> normalize(Vector<Point2D> vect){
		Vector<Point2D> newGesture = new Vector<Point2D>();
		double minX, minY, maxX, maxY;
		minX = maxX = vect.get(0).getX();
		minY = maxY = vect.get(0).getY();
		
		for(int i = 1; i<vect.size(); i++){
			double tmpX = vect.get(i).getX();
			double tmpY = vect.get(i).getY();
			
			minX = minX<tmpX?minX:tmpX;
			minY = minY<tmpY?minY:tmpY;
			
			maxX = maxX>tmpX?maxX:tmpX;
			maxY = maxY>tmpY?maxY:tmpY;
		}
		
		for(int i = 1; i<vect.size(); i++){
			double tmpX = vect.get(i).getX();
			double tmpY = vect.get(i).getY();
			
			double newX, newY;
			newX = ((tmpX-minX)/(maxX - minX)) * 1000;
			newY = ((tmpY-minY)/(maxY - minY)) * 1000;
			newGesture.addElement(new Point2D(newX, newY));
		}

		return newGesture;
	}
	
	
	
	public Matrix calculDtw(Vector<Point2D> gestureX, Vector<Point2D> gestureY){
		int n = gestureX.size();
		int m = gestureY.size();
		Matrix dtw = new Matrix(n, m);
		dtw.items[0][0] = 0;
		for(int i = 1; i<n; i++){
			dtw.items[i][0] = dtw.items[i-1][0] + dist(gestureX.get(i), gestureY.get(0));
			dtw.couple[i][0] = new Couple(i-1, 0);
		}
		
		for(int j = 1; j<m; j++){
			dtw.items[0][j] = dtw.items[0][j-1] + dist(gestureX.get(0), gestureY.get(j));
			dtw.couple[0][j] = new Couple(0, j-1);
		}
		
		for(int i = 1; i<n; i++){
			for(int j = 1; j<m; j++){
				double tmp ;
				int pTmpX, pTmpY;
				if(dtw.items[i][j-1] < dtw.items[i-1][j-1]){
					tmp = dtw.items[i][j-1];
					pTmpX = i;
					pTmpY = j-1;
				} else {
					tmp = dtw.items[i-1][j-1];
					pTmpX = i-1;
					pTmpY = j-1;
				}
				
				if(tmp > dtw.items[i-1][j]){
					tmp = dtw.items[i-1][j];
					pTmpX = i-1;
					pTmpY = j;
				}

				
				/*Le couple est utile pour récupérer la valeur prédentes, c'est à dire la valeur grace a laquelle nous avons obtenue le min*/
				dtw.items[i][j] =dist(gestureX.get(i), gestureY.get(j)) 
						+ tmp;
				dtw.couple[i][j] = new Couple(pTmpX, pTmpY);
			}
		}
		
		return dtw;
	}
	
	public double dist(Point2D a, Point2D b){
		return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
	}
	
	
}
