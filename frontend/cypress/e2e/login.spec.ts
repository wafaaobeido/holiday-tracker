/// <reference types="cypress" />

describe('Login Page', () => {
  beforeEach(() => {
    // Visit the login page (adjust the URL as needed)
    cy.visit('/login');
  });

  it('should display the login form', () => {
    // Verify that the title "Welcome Back" is visible
    cy.get('mat-card-title').contains('Welcome Back');

    // Check that username and password input fields are present
    cy.get('input[name="username"]').should('be.visible');
    cy.get('input[name="password"]').should('be.visible');
  });

  it('should allow a user to login', () => {
    // Fill in the login form
    cy.get('input[name="username"]').type('johndoe');
    cy.get('input[name="password"]').type('password123');

    // Submit the form
    cy.get('button.login-btn').click();

    // Assert that login was successful.
    // For example, if the user is redirected to a dashboard:
    cy.url().should('include', '/dashboard');

    // And a welcome message appears:
    cy.contains('Welcome, johndoe').should('be.visible');
  });
});