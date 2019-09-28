package polix.collection.internal.handler

import polix.reactive.Source

class HandlerUnindexed[G[_] : Source] {
  def mapStream(from: G[])
}
