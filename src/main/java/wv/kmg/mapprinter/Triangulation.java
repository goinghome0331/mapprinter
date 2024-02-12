package wv.kmg.mapprinter;

import java.util.LinkedList;
import java.util.List;

import wv.kmg.mapprinter.proj.Reprojection;

public class Triangulation {
	final int MAX_SUBDIVISION = 10;
	
	String sourceProj;
    String targetProj;
    double[] targetExtent;
	double[] maxSourceExtent;
    
    double destinationResolution;
    double errorThresholdSquared_;
    Reprojection transformInv_;
    List<Triangle> triangles_; 
	public Triangulation(String sourceProj, String targetProj, double[] targetExtent, double[] maxSourceExtent,
			double errorThreshold, double destinationResolution) throws Exception {
		super();
		this.sourceProj = sourceProj;
		this.targetProj = targetProj;
		this.targetExtent = targetExtent;
		this.maxSourceExtent = maxSourceExtent;
		
		this.destinationResolution = destinationResolution;
		
		this.errorThresholdSquared_ = errorThreshold * errorThreshold;
		this.triangles_ = new LinkedList<Triangle>();
		this.transformInv_ = Reprojection.createInstance(targetProj, sourceProj);
		double[] destinationTopLeft = Extent.getTopLeft(targetExtent);
		double[] destinationTopRight = Extent.getTopRight(targetExtent);
		double[] destinationBottomRight = Extent.getBottomRight(targetExtent);
		double[] destinationBottomLeft = Extent.getBottomLeft(targetExtent);
		double[] sourceTopLeft = this.transformInv_.transform(destinationTopLeft);
		double[] sourceTopRight = this.transformInv_.transform(destinationTopRight);
		double[] sourceBottomRight = this.transformInv_.transform(destinationBottomRight);
		double[] sourceBottomLeft = this.transformInv_.transform(destinationBottomLeft);
		
		
		final int maxSubdivision = MAX_SUBDIVISION + (destinationResolution > 0 ? Math.max(0,
				(int) Math.ceil(Math
						.log(Extent.getArea(targetExtent) / (destinationResolution * destinationResolution * 256 * 256))
						/ Math.log(2)))
				: 0);
		this.addQuad_(
			      destinationTopLeft,
			      destinationTopRight,
			      destinationBottomRight,
			      destinationBottomLeft,
			      sourceTopLeft,
			      sourceTopRight,
			      sourceBottomRight,
			      sourceBottomLeft,
			      maxSubdivision
			    );
	}
	public List<Triangle> getTriangles(){
		return this.triangles_;
	}
	public void addTriangle_(double[] a,double[]  b,double[]  c,double[]  aSrc,double[]  bSrc,double[]  cSrc) {
	    this.triangles_.add(new Triangle(
	      new double[][] {aSrc, bSrc, cSrc},
	      new double[][] {a, b, c}
	    ));
	  }
    public void   addQuad_(double[] a, double[] b, double[] c, double[] d, double[] aSrc, double[] bSrc, double[] cSrc, double[] dSrc, double maxSubdivision) throws Exception {

        double[] sourceQuadExtent = Extent.boundingExtent(new double[][] {aSrc, bSrc, cSrc, dSrc});
//        const sourceCoverageX = this.sourceWorldWidth_
//          ? getWidth(sourceQuadExtent) / this.sourceWorldWidth_
//          : null;
//        const sourceWorldWidth = /** @type {number} */ (this.sourceWorldWidth_);

        // when the quad is wrapped in the source projection
        // it covers most of the projection extent, but not fully
//        const wrapsX =
//          this.sourceProj_.canWrapX() &&
//          sourceCoverageX > 0.5 &&
//          sourceCoverageX < 1;

        boolean needsSubdivision = false;

        if (maxSubdivision > 0) {
//          if (this.targetProj_.isGlobal() && this.targetWorldWidth_) {
//            const targetQuadExtent = boundingExtent([a, b, c, d]);
//            const targetCoverageX =
//              getWidth(targetQuadExtent) / this.targetWorldWidth_;
//            needsSubdivision =
//              targetCoverageX > MAX_TRIANGLE_WIDTH || needsSubdivision;
//          }
//          if (!wrapsX && this.sourceProj_.isGlobal() && sourceCoverageX) {
//            needsSubdivision =
//              sourceCoverageX > MAX_TRIANGLE_WIDTH || needsSubdivision;
//          }
        }

        if (!needsSubdivision && this.maxSourceExtent != null) {
          if (
            Double.isFinite(sourceQuadExtent[0]) &&
            Double.isFinite(sourceQuadExtent[1]) &&
            Double.isFinite(sourceQuadExtent[2]) &&
            Double.isFinite(sourceQuadExtent[3])
          ) {
            if (!Extent.intersects(sourceQuadExtent, this.maxSourceExtent)) {
              // whole quad outside source projection extent -> ignore
              return;
            }
          }
        }

        int isNotFinite = 0;

        if (!needsSubdivision) {
          if (
            !Double.isFinite(aSrc[0]) ||
            !Double.isFinite(aSrc[1]) ||
            !Double.isFinite(bSrc[0]) ||
            !Double.isFinite(bSrc[1]) ||
            !Double.isFinite(cSrc[0]) ||
            !Double.isFinite(cSrc[1]) ||
            !Double.isFinite(dSrc[0]) ||
            !Double.isFinite(dSrc[1])
          ) {
            if (maxSubdivision > 0) {
              needsSubdivision = true;
            } else {
              // It might be the case that only 1 of the points is infinite. In this case
              // we can draw a single triangle with the other three points
              isNotFinite =
                (!Double.isFinite(aSrc[0]) || !Double.isFinite(aSrc[1]) ? 8 : 0) +
                (!Double.isFinite(bSrc[0]) || !Double.isFinite(bSrc[1]) ? 4 : 0) +
                (!Double.isFinite(cSrc[0]) || !Double.isFinite(cSrc[1]) ? 2 : 0) +
                (!Double.isFinite(dSrc[0]) || !Double.isFinite(dSrc[1]) ? 1 : 0);
              if (
                isNotFinite != 1 &&
                isNotFinite != 2 &&
                isNotFinite != 4 &&
                isNotFinite != 8
              ) {
                return;
              }
            }
          }
        }

        if (maxSubdivision > 0) {
          if (!needsSubdivision) {
            double[] center = new double[] {(a[0] + c[0]) / 2, (a[1] + c[1]) / 2};
            double[] centerSrc = this.transformInv_.transform(center);

//            let dx;
            double dx = 0;
//            if (wrapsX) {
//              const centerSrcEstimX =
//                (modulo(aSrc[0], sourceWorldWidth) +
//                  modulo(cSrc[0], sourceWorldWidth)) /
//                2;
//              dx = centerSrcEstimX - modulo(centerSrc[0], sourceWorldWidth);
//            } else {
//              dx = (aSrc[0] + cSrc[0]) / 2 - centerSrc[0];
//            }
            dx = (aSrc[0] + cSrc[0]) / 2 - centerSrc[0];
            double dy = (aSrc[1] + cSrc[1]) / 2 - centerSrc[1];
            double centerSrcErrorSquared = dx * dx + dy * dy;
            needsSubdivision = centerSrcErrorSquared > this.errorThresholdSquared_;
          }
          if (needsSubdivision) {
            if (Math.abs(a[0] - c[0]) <= Math.abs(a[1] - c[1])) {
              // split horizontally (top & bottom)
              double[] bc = new double[] {(b[0] + c[0]) / 2, (b[1] + c[1]) / 2};
              double[] bcSrc = this.transformInv_.transform(bc);
              double[] da = new double[] {(d[0] + a[0]) / 2, (d[1] + a[1]) / 2};
              double[] daSrc = this.transformInv_.transform(da);

              this.addQuad_(
                a,
                b,
                bc,
                da,
                aSrc,
                bSrc,
                bcSrc,
                daSrc,
                maxSubdivision - 1
              );
              this.addQuad_(
                da,
                bc,
                c,
                d,
                daSrc,
                bcSrc,
                cSrc,
                dSrc,
                maxSubdivision - 1
              );
            } else {
              // split vertically (left & right)
              double[] ab = new double[] {(a[0] + b[0]) / 2, (a[1] + b[1]) / 2};
              double[] abSrc = this.transformInv_.transform(ab);
              double[] cd = new double[] {(c[0] + d[0]) / 2, (c[1] + d[1]) / 2};
              double[] cdSrc = this.transformInv_.transform(cd);

              this.addQuad_(
                a,
                ab,
                cd,
                d,
                aSrc,
                abSrc,
                cdSrc,
                dSrc,
                maxSubdivision - 1
              );
              this.addQuad_(
                ab,
                b,
                c,
                cd,
                abSrc,
                bSrc,
                cSrc,
                cdSrc,
                maxSubdivision - 1
              );
            }
            return;
          }
        }

//        if (wrapsX) {
//          if (!this.canWrapXInSource_) {
//            return;
//          }
//          this.wrapsXInSource_ = true;
//        }

        // Exactly zero or one of *Src is not finite
        // The triangles must have the diagonal line as the first side
        // This is to allow easy code in reproj.s to make it straight for broken
        // browsers that can't handle diagonal clipping
        if ((isNotFinite & 0xb) == 0) {
          this.addTriangle_(a, c, d, aSrc, cSrc, dSrc);
        }
        if ((isNotFinite & 0xe) == 0) {
          this.addTriangle_(a, c, b, aSrc, cSrc, bSrc);
        }
        if (isNotFinite != 0) {
          // Try the other two triangles
          if ((isNotFinite & 0xd) == 0) {
            this.addTriangle_(b, d, a, bSrc, dSrc, aSrc);
          }
          if ((isNotFinite & 0x7) == 0) {
            this.addTriangle_(b, d, c, bSrc, dSrc, cSrc);
          }
        }
      
    }
    public double[] calculateSourceExtent() {
        double[] extent = Extent.createEmpty();

        this.triangles_.forEach((triangle)-> {
          double[][] src = triangle.getSource();
          Extent.extendCoordinate(extent, src[0]);
          Extent.extendCoordinate(extent, src[1]);
          Extent.extendCoordinate(extent, src[2]);
        });

        return extent;
      }
    
}
