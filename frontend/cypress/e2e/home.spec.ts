/// <reference types="cypress" />

describe('Home Page', () => {
    beforeEach(() => {
      // Visit the dashboard page (adjust the URL if needed)
      cy.visit('/dashboard');
    });

    it('should display the navbar with the Holiday Tracker title and Logout button', () => {
      cy.get('.navbar').should('contain', 'Holiday Tracker');
      cy.get('.navbar .logout-btn').should('be.visible');
    });

    it('should conditionally display the Register button when the user is logged in', () => {
      cy.get('.navbar .register-btn').then($btn => {
        if ($btn.length > 0) {
          cy.wrap($btn).click();
          cy.url().should('include', '/register');
        }
      });
    });

    it('should display two dashboard cards with proper titles, content, and buttons', () => {
      cy.get('.card-container').within(() => {
        cy.get('mat-card').first().within(() => {
          cy.get('mat-card-title').should('contain', 'Your Holidays');
          cy.get('mat-card-content').should('contain', 'Track your planned holidays here.');
          cy.get('button.dashboard-btn').should('contain', 'View Holidays');
        });
        cy.get('mat-card').eq(1).within(() => {
          cy.get('mat-card-title').should('contain', 'My Activity');
          cy.get('mat-card-content').should('contain', 'My login and holiday requests.');
          cy.get('button.dashboard-btn').should('contain', 'View My Activity');
        });
      });
    });

    it('should navigate to the holidays page when clicking "View Holidays"', () => {
      cy.get('.card-container').within(() => {
        cy.contains('View Holidays').click();
      });
      cy.url().should('include', '/holidays');
    });

    it('should navigate to the user logs page when clicking "View My Activity"', () => {
      cy.get('.card-container').within(() => {
        cy.contains('View My Activity').click();
      });
      cy.url().should('include', '/user-logs');
    });
  });
