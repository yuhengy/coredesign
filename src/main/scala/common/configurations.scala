package common

object configurations extends
  TODOConfigurations with
  debugConfigurations
{
}


trait TODOConfigurations
{
  val XLEN = 64
}

trait debugConfigurations
{
  val DEBUG = true
}
