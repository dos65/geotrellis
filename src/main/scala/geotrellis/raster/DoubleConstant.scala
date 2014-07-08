package geotrellis.raster

import geotrellis._

import spire.syntax.cfor._

final case class DoubleConstant(n:Double, cols:Int, rows:Int) extends RasterData {
  def getType = TypeDouble
  def apply(i:Int) = n.asInstanceOf[Int]
  def applyDouble(i:Int) = n
  def length = cols * rows
  def alloc(cols:Int, rows:Int) = DoubleArrayRasterData.empty(cols, rows)
  def mutable = DoubleArrayRasterData(Array.ofDim[Double](length).fill(n), cols, rows)
  def copy = this

  override def combine(other:RasterData)(f:(Int,Int) => Int) = {
    val i = n.toInt
    other.map(z => f(i, z))
  }

  override def map(f:Int => Int) = IntConstant(f(n.toInt), cols, rows)
  override def foreach(f: Int => Unit) = foreachDouble(z => f(z.toInt))

  override def combineDouble(other:RasterData)(f:(Double,Double) => Double) = other.mapDouble(z => f(n, z))
  override def mapDouble(f:Double => Double) = DoubleConstant(f(n), cols, rows)
  override def foreachDouble(f: Double => Unit) = {
    var i = 0
    val len = length
    while (i < len) { f(n); i += 1 }
  }

  def force():RasterData = {
    val forcedData = RasterData.allocByType(getType,cols,rows)
    cfor(0)(_ < cols, _ + 1) { col =>
      cfor(0)(_ < rows, _ + 1) { row =>
        forcedData.setDouble(col,row,n)
      }
    }
    forcedData
  }
  
  def toArrayByte: Array[Byte] = Array(n.toByte)

  def warp(current:RasterExtent,target:RasterExtent):RasterData =
    DoubleConstant(n,target.cols,target.rows)
}