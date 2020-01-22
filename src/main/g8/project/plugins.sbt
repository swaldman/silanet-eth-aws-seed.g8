resolvers += Resolver.mavenLocal
resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.sonatypeRepo("releases")
resolvers += ("mchange-repo" at "https://www.mchange.com/repository")

addSbtPlugin("com.silamoney" % "sbt-silanet-eth-aws" % "$silanet_eth_aws_version$")



