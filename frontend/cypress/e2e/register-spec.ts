/// <reference types="cypress" />

describe('Register Page', () => {
    beforeEach(() => {
      // Visit the registration page (adjust the URL as needed)
      cy.visit('/register');
    });
  
    it('should display the registration form', () => {
      // Check that the title "Register" is visible
      cy.get('mat-card-title').contains('Register');
  
      // Verify that all input fields are present
      cy.get('input[name="firstname"]').should('be.visible');
      cy.get('input[name="lastname"]').should('be.visible');
      cy.get('input[name="username"]').should('be.visible');
      cy.get('input[name="email"]').should('be.visible');
      cy.get('input[name="password"]').should('be.visible');
    });
  
    it('should allow a user to register', () => {
      // Fill in the registration form
      cy.get('input[name="firstname"]').type('John');
      cy.get('input[name="lastname"]').type('Doe');
      cy.get('input[name="username"]').type('johndoe');
      cy.get('input[name="email"]').type('john@example.com');
      cy.get('input[name="password"]').type('password123');
  
      // Submit the form
      cy.get('button.register-btn').click();
  
      // Assert that registration was successful.
      // For example, if a success message is displayed:
      cy.contains('Registration successful').should('be.visible');
  
      // Alternatively, if the app redirects to a dashboard:
      // cy.url().should('include', '/dashboard');
    });
  });