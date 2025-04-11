// frontend/src/services/card.service.js
import authService from './auth.service';

const API_URL = 'http://localhost:8082/api/cards';

class CardService {
  async getAllCards() {
    try {
      const response = await fetch(API_URL, {
        method: 'GET',
        headers: this._getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to fetch cards');
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching cards:', error);
      throw error;
    }
  }

  async getCardById(id) {
    try {
      const response = await fetch(`${API_URL}/${id}`, {
        method: 'GET',
        headers: this._getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to fetch card');
      }

      return await response.json();
    } catch (error) {
      console.error(`Error fetching card ${id}:`, error);
      throw error;
    }
  }

  async getUserCards() {
    const user = authService.getCurrentUser();
    if (!user) {
      throw new Error('User not authenticated');
    }

    try {
      const response = await fetch(`${API_URL}/user/${user.userId}`, {
        method: 'GET',
        headers: this._getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to fetch user cards');
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching user cards:', error);
      throw error;
    }
  }

  async searchCards(keyword) {
    try {
      const response = await fetch(`${API_URL}/search?keyword=${encodeURIComponent(keyword)}`, {
        method: 'GET',
        headers: this._getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to search cards');
      }

      return await response.json();
    } catch (error) {
      console.error('Error searching cards:', error);
      throw error;
    }
  }

  async filterCards(rarity, type) {
    try {
      const response = await fetch(`${API_URL}/filter?rarity=${encodeURIComponent(rarity)}&type=${encodeURIComponent(type)}`, {
        method: 'GET',
        headers: this._getHeaders(),
      });

      if (!response.ok) {
        throw new Error('Failed to filter cards');
      }

      return await response.json();
    } catch (error) {
      console.error('Error filtering cards:', error);
      throw error;
    }
  }

  _getHeaders() {
    const headers = {
      'Content-Type': 'application/json',
    };

    const token = authService.getToken();
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    return headers;
  }
}

export default new CardService();