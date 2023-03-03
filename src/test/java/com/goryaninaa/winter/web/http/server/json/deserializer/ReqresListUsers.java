package com.goryaninaa.winter.web.http.server.json.deserializer;

import java.util.List;
import java.util.Objects;

/**
 * Test entity.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("unused")
public class ReqresListUsers { // NOPMD
  private int page;
  private int perPage;
  private int total;
  private int totalPages;
  private List<Person> data;
  private Support support;

  public ReqresListUsers() { // NOPMD
  }

  /**
   * Test entity constructor.
   *
   * @param page       - page
   * @param perPage    - perPage
   * @param total      - total
   * @param totalPages - totalPages
   * @param data       - data
   * @param support    - support
   */
  public ReqresListUsers(final int page, final int perPage, final int total, final int totalPages,
      final List<Person> data, final Support support) {
    super();
    this.page = page;
    this.perPage = perPage;
    this.total = total;
    this.totalPages = totalPages;
    this.data = data;
    this.support = support;
  }

  public int getPage() {
    return page;
  }

  public void setPage(final int page) {
    this.page = page;
  }

  public int getPerPage() {
    return perPage;
  }

  public void setPerPage(final int perPage) {
    this.perPage = perPage;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(final int total) {
    this.total = total;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(final int totalPages) {
    this.totalPages = totalPages;
  }

  public List<Person> getData() {
    return data;
  }

  public void setData(final List<Person> data) {
    this.data = data;
  }

  public Support getSupport() {
    return support;
  }

  public void setSupport(final Support support) {
    this.support = support;
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, page, perPage, support, total, totalPages);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true; // NOPMD
    }
    if (obj == null) {
      return false; // NOPMD
    }
    if (getClass() != obj.getClass()) {
      return false; // NOPMD
    }
    final ReqresListUsers other = (ReqresListUsers) obj;
    return this.getData().containsAll(other.getData()) && page == other.page
        && perPage == other.perPage && Objects.equals(support, other.support)
        && total == other.total && totalPages == other.totalPages;
  }

}
