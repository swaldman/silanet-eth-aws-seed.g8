import java.io.File
import com.silamoney.silanet.eth._
import com.silamoney.silanet.eth.Default  // resolve ambiguity with sbt.Default
import com.silamoney.silanet.eth.aws._
import com.silamoney.silanet.eth.aws.Credentials // resolve ambiguity with sbt.Credentials
import com.mchange.sc.v1.consuela.ethereum.{EthAddress,EthPrivateKey}
import com.mchange.sc.v1.consuela.ethereum.net.{Enode,IPv4Address,IPAddress}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ec2.model.{AvailabilityZone,InstanceType}
import scala.collection._

name := "$name$"

// This is the main event. Play around with modifying this!

silanetNextSpecification := {
  val specUid = name.value

  val _nodes : immutable.Set[UserSpecification.Node] = immutable.Set (
    UserSpecification.Node.Validating (
      uid       = "validator-0",
      region    = Region.$admin_region$,
      // nondefaultVpc = Some( NondefaultVpc( vpcId = "vpc-0fe55ef77f1ff566e", subnetId = "subnet-04b599470c9b287e6" ) )
    ),
    UserSpecification.Node.Nonvalidating (
      uid                  = "public-0",
      region               = Region.$admin_region$,
      // elasticIp            = Some(IPAddress("3.20.134.190")),
      isArchive            = true,
      exportsWebSocket     = true,
      exportsBlockExplorer = true,
      // nondefaultVpc = Some( NondefaultVpc( vpcId = "vpc-0fe55ef77f1ff566e", subnetId = "subnet-02c787aec5778062f" ) ),
      nginxProxyServices   = NginxProxyServices.Http // Https, HttpsWithBasicAuth, Http, or Off, but for any Https you'd need to provide certs (see below)
    )
  )

  // place as many address as you'd like in here, comma separated
  val faucets = immutable.Set (
    EthAddress("0xD72299b527f9a4bd075D2304C627d7CE21D32Cec") // this is a well-known -- meaning really insecure -- test address!
  )

  val initialGenesis = Default.genesis( faucets, chainId = $chainId$ )

  UserSpecification (
    uid                             = specUid,
    adminRegion                     = Region.$admin_region$,
    adminRegionImageId              = "$admin_region_image_id$",
    defaultSshCidrIpV4Range         = "0.0.0.0/0",
    defaultExtraIngressRules        = immutable.Set.empty[IngressRule],
    defaultExtraAuthorizedKeys      = immutable.Set.empty[String],
    defaultInstanceType             = InstanceType.T2_MEDIUM,
    defaultInitialDataVolSizeGb     = 16,
    defaultKeyName                  = "$default_key_name$",
    nodes                           = _nodes,
    unmanagedPeers                  = immutable.Set.empty[Enode],
    unmanagedPeersNonvalidatingOnly = immutable.Set.empty[Enode],
    genesis                         = initialGenesis
  )
}

// YOU DO NEED THIS
// modify this so that the keyname in your specification points to your keyfile.pem
// it can, but does not have to be, in this secrets directory
silanetKeyFiles := immutable.Map (
  "$default_key_name$" -> new File("secrets/$default_key_name$.pem")
)

// Only required if you wish services to be exported by https
/*
silanetCertificateSource := {
  SslCertificateSource.Wildcard( new File( "secrets/silanet.rocks-wildcard-cert/fullchain.pem" ), new File( "secrets/silanet.rocks-wildcard-cert/privkey.pem" ) )
}
*/


// Only required if you wish services to be exported by https
// Needed to automatically set the session URL if all nonvalidators export only Https
/*
silanetHttpsNonvalidatorUrlHostName := "myhost.mydomain.com"
*/ 

// Only required if you want different credentials/ownership for different resources
// Otherwise, just place your AWS credentials in their usual home

/*
silanetCredentials := {
  val specUid = name.value

  val defaultCredentials = Credentials.fromPropertiesFile( new File("secrets/steve-sila-aws.properties") )

  ByUidCredentials.builder()
    .addProvider(specUid, defaultCredentials)
    .defaultUid(specUid)
    .build()
}
*/

// For testnets only, provide some addresses (in this case the faucet) we don't have to bother unlocking
ethcfgPublicInsecureTestAccounts := immutable.Set(
  EthPrivateKey("0x7d3a2ee6be553c58e72e2adf38a96ae8e26eb7678f8c55772034720e96cab250") // 0xD72299b527f9a4bd075D2304C627d7CE21D32Cec
)

// so our faucet is our default account
ethcfgAddressSender := "0xD72299b527f9a4bd075D2304C627d7CE21D32Cec"

// so our chain is the current chain
ethcfgNodeChainId := $chainId$

// so sbt-ethereum doesn't ask us to make a wallet, download a compiler, on startup
ethcfgSuppressInteractiveStartup := true 

