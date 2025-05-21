import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import react from 'eslint-plugin-react'
import jsxa11y from 'eslint-plugin-jsx-a11y'

export default [
  { ignores: ['dist'] },
  {
    files: ['**/*.{js,jsx}'],
    languageOptions: {
      ecmaVersion: 2020,
      globals: globals.browser,
      parserOptions: {
        ecmaVersion: 'latest',
        ecmaFeatures: { jsx: true },
        sourceType: 'module',
      },
    },
    plugins: {
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
      'jsx-a11y': jsxa11y,
      'react': react,
    },
    rules: {
      'react/jsx-uses-vars': 'error',
      ...js.configs.recommended.rules,
      ...reactHooks.configs.recommended.rules,
      'no-unused-vars': ['error', { varsIgnorePattern: '^[A-Z_]' }],
      'react-refresh/only-export-components': [
        'warn',
        { allowConstantExport: true },
      ],
      "react/no-multi-comp": [2, { "ignoreStateless": true }],
      "react/forbid-prop-types": [2, { "forbid": ['array', 'object'], "checkContextTypes": true, "checkChildContextTypes": true }],
      "react/prefer-es6-class": [2, "always"],
      "react/prefer-stateless-function": [2, { "ignorePureComponents": true }],
      "react/jsx-filename-extension": [2, { "allow": "as-needed" }],
      "react/jsx-pascal-case": [2, { allowAllCaps: false, allowNamespace: false, allowLeadingUnderscore: false }],
      "react/jsx-closing-bracket-location": [2],
      "react/jsx-closing-tag-location": [2],
      "react/jsx-tag-spacing": [2, { "closingSlash": "never", "beforeSelfClosing": "always", "afterOpening": "never" }],
      "react/jsx-curly-spacing": [2, {"when": "never"}],
      "react/jsx-boolean-value": [2, "never"],
      "jsx-a11y/alt-text": [2],
      "jsx-a11y/img-redundant-alt": [2],
      "jsx-a11y/no-access-key": [2],
      "react/no-array-index-key": [2],
      "react/no-string-refs": [2],
      "react/jsx-wrap-multilines": [2],
      "react/self-closing-comp": [2],
      "react/jsx-no-bind": [2, {"allowArrowFunctions": true}],
      "react/require-render-return": [2],
      "react/sort-comp": [1],
      "react/no-is-mounted": [2]
    },
  },
]
