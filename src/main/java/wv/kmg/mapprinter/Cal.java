package wv.kmg.mapprinter;

public class Cal {
	public static final int DECIMALS = 5;
	public static double toRadians(double angleInDegrees) {
		return (angleInDegrees * Math.PI) / 180;
	}
	public static double toFixed(double n,int decimals) {
		  double factor = Math.pow(10, decimals);
		  return Math.round(n * factor) / factor;
	}

	public static double floor(double n, int decimals) {
		return Math.floor(toFixed(n, decimals));
	}

	public static double ceil(double n, int decimals) {
		return Math.ceil(toFixed(n, decimals));
	}

	public static double clamp(double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
	}
	// 연립 일차방정식 풀이(가우스 소거법-아핀 계수 계산시)
	public static double[] solveLinearSystem(double[][] mat) {
		int n = mat.length;

		for (int i = 0; i < n; i++) {
			// Find max in the i-th column (ignoring i - 1 first rows)
			int maxRow = i;
			double maxEl = Math.abs(mat[i][i]);
			for (int r = i + 1; r < n; r++) {
				double absValue = Math.abs(mat[r][i]);
				if (absValue > maxEl) {
					maxEl = absValue;
					maxRow = r;
				}
			}

			if (maxEl == 0) {
				return null; // matrix is singular
			}

			// Swap max row with i-th (current) row
			double[] tmp = mat[maxRow];
			mat[maxRow] = mat[i];
			mat[i] = tmp;

			// Subtract the i-th row to make all the remaining rows 0 in the i-th column
			for (int j = i + 1; j < n; j++) {
				double coef = -mat[j][i] / mat[i][i];
				for (int k = i; k < n + 1; k++) {
					if (i == k) {
						mat[j][k] = 0;
					} else {
						mat[j][k] += coef * mat[i][k];
					}
				}
			}
		}

		// Solve Ax=b for upper triangular matrix A (mat)
		double[] x = new double[n];
		for (int l = n - 1; l >= 0; l--) {
			x[l] = mat[l][n] / mat[l][l];
			for (int m = l - 1; m >= 0; m--) {
				mat[m][n] -= mat[m][l] * x[l];
			}
		}
		return x;
	}
}
