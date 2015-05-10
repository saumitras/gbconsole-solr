package models

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.connection.channel.direct.Session
import net.schmizz.sshj.userauth.keyprovider.KeyProvider
import net.schmizz.sshj.connection.channel.direct.Session.Command

import java.io.File
import java.io.IOException


object SSHUtils extends App {

      val ssh:SSHClient = new SSHClient()
      ssh.loadKnownHosts()
      ssh.connect("10.172.137.122")

      val username = "saumitra"
      val privateKey = new File("/home/sam/.ssh/id_rsa")

      val keys:KeyProvider = ssh.loadKeys(privateKey.getPath())

      ssh.authPublickey(username, keys)

      val session:Session = ssh.startSession()

      val cmd:Command = session.exec("du -sh /tmp/*")

      val output:String = IOUtils.readFully(cmd.getInputStream()).toString()

      session.close()
      ssh.disconnect()

}
