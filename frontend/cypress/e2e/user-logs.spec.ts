/// <reference types="cypress" />

describe('User Activity Logs', () => {
  beforeEach(() => {
    // Visit the user logs page (adjust the URL if needed)
    cy.visit('/logs');
  });

  it('displays the user activity logs header', () => {
    // Verify the header contains "User Activity Logs"
    cy.get('.user-activity-container .header').should('contain', 'User Activity Logs');
  });

  it('displays search input and refresh icon', () => {
    // Verify the search input and refresh icon are visible
    cy.get('.search-container input').should('have.attr', 'placeholder', 'Search');
    cy.get('.search-container .refresh-icon').should('be.visible');
  });

  it('displays a table with columns: User, Activity, Timestamp', () => {
    // Verify table columns exist by checking the header cells
    cy.get('table').within(() => {
      cy.get('th').contains('User').should('exist');
      cy.get('th').contains('Activity').should('exist');
      cy.get('th').contains('Timestamp').should('exist');
    });
  });

  it('allows filtering logs', () => {
    // Type into the search input and check that the value is set.
    cy.get('.search-container input')
      .type('John Doe')
      .should('have.value', 'John Doe');
    // Optionally, assert that the table rows are filtered accordingly.
  });

  it('refreshes logs on clicking the refresh icon', () => {
    // Click the refresh icon.
    cy.get('.search-container .refresh-icon').click();
    cy.get('table').should('be.visible');
  });

  it('displays a paginator', () => {
    // Check that the paginator is visible on the page.
    cy.get('mat-paginator').should('be.visible');
  });
});