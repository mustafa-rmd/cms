package com.thmanyah.services.cms.services;

import com.thmanyah.services.cms.model.Show;

/**
 * Service interface for publishing events related to show operations. This service handles
 * publishing events to message queues for other services to consume.
 */
public interface EventPublisherService {

  /**
   * Publish show created event
   *
   * @param show The show that was created
   */
  void publishShowCreatedEvent(Show show);

  /**
   * Publish show updated event
   *
   * @param show The show that was updated
   */
  void publishShowUpdatedEvent(Show show);

  /**
   * Publish show deleted event
   *
   * @param show The show that was deleted
   */
  void publishShowDeletedEvent(Show show);
}
