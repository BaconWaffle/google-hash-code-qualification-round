import scala.collection.mutable.Map
import scala.collection.immutable.{Map => IMap}

case class Drone(id: Int, var position: (Int, Int), var products: Map[Int, Int]) {
  var isBusy = false
  var stopsBeingBusyAt = 0

  override def equals(o: Any) = o match {
    case that: Drone => that.id == id
    case _ => false
  }
  override def hashCode = id

  def updateState(): Unit = {
    if (isBusy && stopsBeingBusyAt == Simulation.instance.currentTime) {
      isBusy = false
    }
  }

  def totalWeightOfProducts: Int = Simulation.instance.weightOfProducts(products)
  def currentCapacity: Int = Simulation.instance.droneCapacity - totalWeightOfProducts

  def getProductsThatCanFitOutOf(newProducts: Map[Int, Int]): Map[Int, Int] = {
    val canCarry = Map[Int, Int]()
    var ps = newProducts.toArray
    // we load drone in descending order of product weights
    ps = ps.sortWith((a: (Int, Int), b: (Int, Int)) => {
      Simulation.instance.productTypeWeights(a._1) > Simulation.instance.productTypeWeights(b._1)
    })
    
    var capacity = currentCapacity
    for ((productType, quantity) <- ps) {
      for (i <- 0 until quantity) {
        val weight = Simulation.instance.productTypeWeights(productType)
        if (weight <= capacity) {
          canCarry(productType) = canCarry.getOrElse(productType, 0) + 1
          capacity -= weight
        }
      }
    }
    canCarry
  }

  def addProducts(newProducts: IMap[Int, Int]): Unit = {
    for ((productType, quantity) <- newProducts) {
      products(productType) = products.getOrElse(productType, 0) + quantity
    }
  }
  def addProducts(newProducts: Map[Int, Int]): Unit = {
    addProducts(newProducts.toMap)
  }

  def removeProducts(ps: Map[Int, Int]): Unit = {
    for ((productType, quantity) <- ps) {
      products(productType) -= quantity
    }
  }

  def removeAllProducts(): Unit = {
    products = Map[Int, Int]()
  }

  def distanceFrom(other: (Int, Int)) = {
    Simulation.instance.getDistance(position, other)
  }
}
