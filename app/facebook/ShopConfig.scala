package facebook

/**
  * Holds configuration for the store
  *
  * Created by andref on 26-06-2017.
  *
  * @param storeId
  * @param PAGE_ACCESS_TOKEN Facebook Page Access Token
  *
  */
case class ShopConfig( storeId: Long, // System store ID
                       PAGE_ACCESS_TOKEN: String // Facebook Page ACCESS Token
                     ) {
}
