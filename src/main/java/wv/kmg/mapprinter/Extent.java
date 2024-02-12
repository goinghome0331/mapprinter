package wv.kmg.mapprinter;

import java.util.List;
// extent 계산 모듈
public class Extent {
	//extent 왼쪽위
	public static double[] getTopLeft(double[] extent) {
		return new double[] { extent[0], extent[3] };
	}
	//extent 오른쪽위
	public static double[] getTopRight(double[] extent) {
		return new double[] { extent[2], extent[3] };
	}
	//extent 왼쪽아래
	public static double[] getBottomLeft(double[] extent) {
		return new double[] { extent[0], extent[1] };
	}
	//extent 오른쪽아래
	public static double[] getBottomRight(double[] extent) {
		return new double[] { extent[2], extent[1] };
	}
	//extent 너비
	public static double getWidth(double[] extent) {
		return extent[2] - extent[0];
	}
	//extent 높이
	public static double getHeight(double[] extent) {
		return extent[3] - extent[1];
	}
	//extent 넓이
	public static double getArea(double[] extent) {
		double area = 0;
		if (!isEmpty(extent)) {
			area = getWidth(extent) * getHeight(extent);
		}
		return area;
	}
	//extent 유효성 체크
	public static boolean isEmpty(double[] extent) {
		return extent[2] < extent[0] || extent[3] < extent[1];
	}
	//extent 여러 extent를 합쳐서 하나의 extent로
	public static double[] boundingExtent(double[][] coordinates) {
		double[] extent = createEmpty();
		for (int i = 0, ii = coordinates.length; i < ii; ++i) {
			extendCoordinate(extent, coordinates[i]);
		}
		return extent;
	}
	// 빈값 extent 만들기
	public static double[] createEmpty() {
		return new double[] { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY };
	}
	// coordinate 기반 extent 확장하기
	public static void extendCoordinate(double[] extent, double[] coordinate) {
		if (coordinate[0] < extent[0]) {
			extent[0] = coordinate[0];
		}
		if (coordinate[0] > extent[2]) {
			extent[2] = coordinate[0];
		}
		if (coordinate[1] < extent[1]) {
			extent[1] = coordinate[1];
		}
		if (coordinate[1] > extent[3]) {
			extent[3] = coordinate[1];
		}
	}
	// extent 교차 여부 확인
	public static boolean intersects(double[] extent1, double[] extent2) {
		return (extent1[0] <= extent2[2] && extent1[2] >= extent2[0] && extent1[1] <= extent2[3]
				&& extent1[3] >= extent2[1]);
	}
	// 두개 extent으로 확장하기
	public static double[] extend(double[] extent1, double[] extent2) {
		if (extent2[0] < extent1[0]) {
			extent1[0] = extent2[0];
		}
		if (extent2[2] > extent1[2]) {
			extent1[2] = extent2[2];
		}
		if (extent2[1] < extent1[1]) {
			extent1[1] = extent2[1];
		}
		if (extent2[3] > extent1[3]) {
			extent1[3] = extent2[3];
		}
		return extent1;
	}
	// 두개 extent으로 확장하기
	public static double[] createOrUpdate(double minX, double minY, double maxX, double maxY, double[] dest) {
		if (dest != null) {
			dest[0] = minX;
			dest[1] = minY;
			dest[2] = maxX;
			dest[3] = maxY;
			return dest;
		} else {
			return new double[] { minX, minY, maxX, maxY };
		}
	}
	// String 기반 4개 코너 중 값 얻기
	public static double[] getCorner(double[] extent, String corner) {
		double[] coordinate;
		if (corner.equals("bottom-left")) {
			coordinate = getBottomLeft(extent);
		} else if (corner.equals("bottom-right")) {
			coordinate = getBottomRight(extent);
		} else if (corner.equals("top-left")) {
			coordinate = getTopLeft(extent);
		} else if (corner.equals("top-right")) {
			coordinate = getTopRight(extent);
		} else {
			return null;
		}
		return coordinate;
	}
	// extent center 값 얻기
	public static double[] getCenter(double[] extent) {
		return new double[] { (extent[0] + extent[2]) / 2, (extent[1] + extent[3]) / 2 };
	}
	// coordinate 값이 extent에 포함되지는 지
	public static boolean containsCoordinate(double[] extent, double[] coordinate) {
		return containsXY(extent, coordinate[0], coordinate[1]);
	}
	// x,y 좌표값이 extent에 포함되지는 지
	public static boolean containsXY(double[] extent, double x, double y) {
		return extent[0] <= x && x <= extent[2] && extent[1] <= y && y <= extent[3];
	}
	// 두개의 extent가 교차하는 extent 값 얻기
	public static double[] getIntersection(double[] extent1, double[] extent2, double[] dest) {
		double[] intersection = dest != null ? dest : createEmpty();
		if (intersects(extent1, extent2)) {
			if (extent1[0] > extent2[0]) {
				intersection[0] = extent1[0];
			} else {
				intersection[0] = extent2[0];
			}
			if (extent1[1] > extent2[1]) {
				intersection[1] = extent1[1];
			} else {
				intersection[1] = extent2[1];
			}
			if (extent1[2] < extent2[2]) {
				intersection[2] = extent1[2];
			} else {
				intersection[2] = extent2[2];
			}
			if (extent1[3] < extent2[3]) {
				intersection[3] = extent1[3];
			} else {
				intersection[3] = extent2[3];
			}
		} else {
			return createOrUpdateEmpty(intersection);
		}
		return intersection;
	}
	// flatCoordinates 기반으로 extent 만들기
	public static double[] createOrUpdateFromFlatCoordinates(List<Double> flatCoordinates, int offset, int end,
			int stride, double[] dest) {
		double[] extent = createOrUpdateEmpty(dest);
		return extendFlatCoordinates(extent, flatCoordinates, offset, end, stride);
	}
	// flatCoordinates 기반으로 extent 확장하기
	public static double[] extendFlatCoordinates(double[] extent, List<Double> flatCoordinates, int offset, int end,
			int stride) {
		for (; offset < end; offset += stride) {
			extendXY(extent, flatCoordinates.get(offset), flatCoordinates.get(offset + 1));
		}
		return extent;
	}
	// x,y 좌표값으로 extent 확장하기
	public static void extendXY(double[] extent, double x, double y) {
		extent[0] = Math.min(extent[0], x);
		extent[1] = Math.min(extent[1], y);
		extent[2] = Math.max(extent[2], x);
		extent[3] = Math.max(extent[3], y);
	}
	// x,y 좌표값으로 extent 확장하기
	public static double[] createOrUpdateEmpty(double[] dest) {
		return createOrUpdate(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
				Double.NEGATIVE_INFINITY, dest);
	}
	// extent 매개변수 값으로 업데이트 하기
	public static double[] returnOrUpdate(double[] extent, double[] dest) {
		if (dest != null) {
			dest[0] = extent[0];
			dest[1] = extent[1];
			dest[2] = extent[2];
			dest[3] = extent[3];
			return dest;
		} else {
			return extent;
		}
	}
	// extent buffer 값만큼 확장하기
	public static double[] buffer(double[] extent, double value, double[] dest) {
		if (dest != null) {
			dest[0] = extent[0] - value;
			dest[1] = extent[1] - value;
			dest[2] = extent[2] + value;
			dest[3] = extent[3] + value;
			return dest;
		} else {
			return new double[] { extent[0] - value, extent[1] - value, extent[2] + value, extent[3] + value, };
		}
	}
	// center를 중심으로 resolution과 px 단위의 width,height를 이욯아여 extent 구하기(rotation을 통해 회전 가능)
	public static double[] getForViewAndSize(double[] center, double resolution, double rotation, double[] size,
			double[] dest) {
		double[] viewport = getRotatedViewport(center, resolution, rotation, size);
		return createOrUpdate(Math.min(Math.min(viewport[0], viewport[2]), Math.min(viewport[4], viewport[6])),
				Math.min(Math.min(viewport[1], viewport[3]), Math.min(viewport[5], viewport[7])),
				Math.max(Math.max(viewport[0], viewport[2]), Math.max(viewport[4], viewport[6])),
				Math.max(Math.max(viewport[1], viewport[3]), Math.max(viewport[5], viewport[7])), dest);
	}

	public static double[] getRotatedViewport(double[] center, double resolution, double rotation, double[] size) {
		double dx = (resolution * size[0]) / 2;
		double dy = (resolution * size[1]) / 2;
		double cosRotation = Math.cos(rotation);
		double sinRotation = Math.sin(rotation);
		double xCos = dx * cosRotation;
		double xSin = dx * sinRotation;
		double yCos = dy * cosRotation;
		double ySin = dy * sinRotation;
		double x = center[0];
		double y = center[1];
		return new double[] { x - xCos + ySin, y - xSin - yCos, x - xCos - ySin, y - xSin + yCos, x + xCos - ySin,
				y + xSin + yCos, x + xCos + ySin, y + xSin - yCos, x - xCos + ySin, y - xSin - yCos, };
	}

}
