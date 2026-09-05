import { describe, it, expect, beforeEach, vi } from 'vitest';
import { getToken, setToken, clearToken, isAuthenticated, apiFetch } from '../api';

describe('api.js', () => {
  beforeEach(() => {
    sessionStorage.clear();
    vi.restoreAllMocks();
  });

  describe('token management', () => {
    it('getToken returns null when no token is set', () => {
      expect(getToken()).toBeNull();
    });

    it('setToken stores token in sessionStorage', () => {
      setToken('test-token-123');
      expect(getToken()).toBe('test-token-123');
    });

    it('clearToken removes token from sessionStorage', () => {
      setToken('test-token');
      clearToken();
      expect(getToken()).toBeNull();
    });

    it('isAuthenticated returns false when no token', () => {
      expect(isAuthenticated()).toBe(false);
    });

    it('isAuthenticated returns true when token exists', () => {
      setToken('some-token');
      expect(isAuthenticated()).toBe(true);
    });
  });

  describe('apiFetch', () => {
    it('includes Authorization header when token exists', async () => {
      setToken('my-token');

      const mockFetch = vi.fn().mockResolvedValue({
        ok: true,
        status: 200,
        json: () => Promise.resolve({}),
      });

      globalThis.fetch = mockFetch;

      await apiFetch('/servers');

      expect(mockFetch).toHaveBeenCalledWith(
        '/api/servers',
        expect.objectContaining({
          headers: expect.objectContaining({
            Authorization: 'Bearer my-token',
          }),
        })
      );
    });

    it('does not include Authorization header when no token', async () => {
      const mockFetch = vi.fn().mockResolvedValue({
        ok: true,
        status: 200,
        json: () => Promise.resolve({}),
      });

      globalThis.fetch = mockFetch;

      await apiFetch('/servers');

      expect(mockFetch).toHaveBeenCalledWith(
        '/api/servers',
        expect.objectContaining({
          headers: expect.not.objectContaining({
            Authorization: expect.any(String),
          }),
        })
      );
    });

    it('clears token and redirects on 401', async () => {
      setToken('expired-token');

      const mockFetch = vi.fn().mockResolvedValue({
        ok: false,
        status: 401,
      });

      globalThis.fetch = mockFetch;

      const mockAssign = vi.fn();
      Object.defineProperty(window, 'location', {
        value: { href: '', assign: mockAssign },
        writable: true,
      });

      await expect(apiFetch('/protected')).rejects.toThrow(
        'Sessão expirada.'
      );

      expect(getToken()).toBeNull();
    });
  });
});
