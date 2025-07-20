package com.thmanyah.services.cms.model;

/** User roles for CMS access control */
public enum Role {
  /**
   * Administrator - Full access to all operations including user management, imports, and deletions
   */
  ADMIN,

  /** Editor - Can create, read, and update shows but cannot delete or manage imports/users */
  EDITOR
}
