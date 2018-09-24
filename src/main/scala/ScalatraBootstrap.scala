import ice.master.datawarehouse._
import ice.master.datawarehouse.servlets.DatawarehouseServlet
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new DatawarehouseServlet, "/*")
    context.initParameters("org.scalatra.cors.enable") = "true"
  }
}
