package ice.master.datawarehouse

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.{MultipartConfig, ScalatraListener}

/**
  * Configuration and launch of the server.
  */
object JettyLauncher {
  def main(args: Array[String]) {
    val port = 8081
    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
    context.setResourceBase("src/main/webapp")
    context.addEventListener(new ScalatraListener)
    val holder = context.addServlet(classOf[DefaultServlet], "/")

    holder.getRegistration.setMultipartConfig(
      MultipartConfig(
        maxFileSize = Some(3*1024*1024),
        fileSizeThreshold = Some(1*1024*1024)
      ).toMultipartConfigElement
    )
    server.setHandler(context)

    server.start()
    server.join()
  }
}
