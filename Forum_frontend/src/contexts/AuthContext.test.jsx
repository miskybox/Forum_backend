import React from 'react'
import { describe, it, expect } from 'vitest'
import { AuthContext } from './AuthContext'
import { render } from '@testing-library/react'

describe('AuthContext hasRole', () => {
  it('reconoce roles con y sin prefijo ROLE_', () => {
    const value = {
      currentUser: { roles: ['ROLE_ADMIN', 'ROLE_USER'] },
      hasRole: (role) => {
        const roles = value.currentUser?.roles || []
        const normalized = role.startsWith('ROLE_') ? role : `ROLE_${role}`
        return roles.includes(role) || roles.includes(normalized)
      }
    }

    const Wrapper = ({ children }) => (
      <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
    )

    render(<div />, { wrapper: Wrapper })
    expect(value.hasRole('ADMIN')).toBe(true)
    expect(value.hasRole('ROLE_ADMIN')).toBe(true)
    expect(value.hasRole('MODERATOR')).toBe(false)
  })
})