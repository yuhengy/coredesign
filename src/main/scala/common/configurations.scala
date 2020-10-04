package common

object configurations extends
  TODOConfigurations with
  debugConfigurations
{
}


trait TODOConfigurations
{
  val XLEN = 64  //TODO: for now, signed/unsigned extend do not use this configuration
}

trait debugConfigurations
{
  val DEBUG = true
}
