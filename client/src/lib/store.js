import { writable } from 'svelte/store'

/** @typedef {Object} User
 * @property {boolean} admin
 * @property {string} email
 * @property {string} name
 * @property {boolean} anonymous
 */

/** @type {import('svelte/store').Writable<User|null>} */
export const user = writable(null);
export const adminState = writable('off');
