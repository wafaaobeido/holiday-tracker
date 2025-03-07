/// <reference types="cypress" />

describe('Holiday List', () => {
  beforeEach(() => {
    // Visit the holiday list page (adjust the URL as needed)
    cy.visit('/holidays');
  });

  it('displays the Holidays header', () => {
    cy.get('.holidays-container .header').should('contain', 'Holidays');
  });

  it('displays input fields for holiday search parameters', () => {
    cy.get('.input-container').within(() => {
      cy.get('input[name="year"]').should('exist');
      cy.get('input[name="countryCode"]').should('exist');
      cy.get('input[name="number"]').should('exist');
      cy.get('input[name="country2"]').should('exist');
    });
    cy.get('button.input-btn').contains('Search').should('be.visible');
  });

  it('allows the user to perform a holiday search', () => {
    cy.get('input[name="year"]').clear().type('2025');
    cy.get('input[name="countryCode"]').clear().type('US');
    cy.get('input[name="number"]').clear().type('5');
    cy.get('input[name="country2"]').clear().type('UK');
    cy.get('button.input-btn').click();
    cy.get('table').should('be.visible');
  });

  it('displays the country filter dropdown when available', () => {
    cy.get('.container').within(() => {
      cy.get('mat-form-field mat-select').should('be.visible');
      cy.get('mat-select').click();
      cy.get('mat-option').its('length').should('be.gt', 0);
      cy.get('body').click(0, 0);
    });
  });

  it('displays a table with columns: Country, Name, Date', () => {
    cy.get('table').within(() => {
      cy.get('th').contains('Country').should('exist');
      cy.get('th').contains('Name').should('exist');
      cy.get('th').contains('Date').should('exist');
    });
  });

  it('displays error messages if present', () => {
    cy.get('.error-messages').then(($el) => {
      if ($el.length > 0) {
        cy.wrap($el).should('contain', 'Error for');
      }
    });
  });

  it('displays a paginator', () => {
    cy.get('mat-paginator').should('be.visible');
  });
});